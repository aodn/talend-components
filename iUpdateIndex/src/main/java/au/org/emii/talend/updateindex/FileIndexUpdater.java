package au.org.emii.talend.updateindex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import au.org.emii.talend.JobRunNos;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class FileIndexUpdater {

    private Connection conn;
    private String jobName;
    private String schemaName;
    private long runNo;
    private long jobId;
    private long updatedFileCount;
    private long newFileCount;
    private long deletedFileCount;
    private PreparedStatement pstmtUpdate;
    private PreparedStatement pstmtInsert;
    private PreparedStatement pstmtMarkDeleted;
    

    public FileIndexUpdater(Connection conn, String schemaName, String jobName) {
        this.conn = conn;
        this.jobName = jobName;
        this.schemaName = schemaName;

        runLiquibase();
        determineJobIdAndRunNumber();
        prepareStatements();
    }

    public void addOrUpdateFile(FileResource file) {
        try {
            String url = file.getRelativePath();
            Timestamp lastModified = new Timestamp(file.getLastModified().getTime());
            double size = file.getSize();

            // try to update an existing record

            pstmtUpdate.setTimestamp(1, lastModified);
            pstmtUpdate.setDouble(2, size);
            pstmtUpdate.setTimestamp(3, currentTime());
            pstmtUpdate.setLong(4, runNo);
            pstmtUpdate.setLong(5, jobId);
            pstmtUpdate.setString(6, url);

            boolean recordUpdated = pstmtUpdate.executeUpdate() > 0;

            // check result

            if (recordUpdated) {
                updatedFileCount++;
            } else {
                pstmtInsert.setLong(1, jobId);
                pstmtInsert.setString(2, url);
                pstmtInsert.setTimestamp(3, lastModified);
                pstmtInsert.setDouble(4, size);
                pstmtInsert.setTimestamp(5, currentTime());
                pstmtInsert.setLong(6, runNo);

                pstmtInsert.executeUpdate();

                updatedFileCount++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(FileResource file) {
        try {
            pstmtMarkDeleted.setTimestamp(1, currentTime());
            pstmtMarkDeleted.setLong(2, runNo);
            pstmtMarkDeleted.setLong(3, jobId);
            pstmtMarkDeleted.setString(4, file.getRelativePath());

            boolean recordMarked = pstmtMarkDeleted.executeUpdate() > 0;

            if (!recordMarked) {
                System.err.print("FILE_INDEX_UPDATER - WARNING: " + file.getRelativePath() + " not found on index");
            }

            deletedFileCount++;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getUpdatedFileCount() {
        return updatedFileCount;
    }

    public long getNewFileCount() {
        return newFileCount;
    }

    public long getDeletedFileCount() {
        return deletedFileCount;
    }

    public long getProcessedFileCount() {
        return newFileCount + updatedFileCount + deletedFileCount;
    }

    private void runLiquibase() {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            database.setDefaultSchemaName(schemaName); 
            Liquibase liquibase = new Liquibase("iPostgresqlIndexResources_changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(null);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    private void determineJobIdAndRunNumber() {
        try {
           // Try getting the run number by adding 1 to the last run number for this job

            String updateStmt = "UPDATE index_job "
                    + " SET last_run_no = last_run_no + 1, last_run_date = ? WHERE name = ?";

            PreparedStatement pstmtUpdate = conn.prepareStatement(updateStmt);

            pstmtUpdate.setTimestamp(1, currentTime());
            pstmtUpdate.setString(2, jobName);

            boolean noIndexJobRecord = pstmtUpdate.executeUpdate() == 0;

            // If there was no index_job record for this job create a new record setting the run number to 1

            if (noIndexJobRecord) {
                String insertStmt = "INSERT INTO index_job "
                        + " (name, last_run_no, last_run_date) VALUES (?,?,?)";
                PreparedStatement pstmtInsert = conn
                        .prepareStatement(insertStmt);
                pstmtInsert.setString(1, jobName);
                pstmtInsert.setLong(2, 1);
                pstmtInsert.setTimestamp(3, currentTime());
                pstmtInsert.executeUpdate();
            }

            // Get job id and run number to be used for this run (just recorded above)

            Statement stmt = conn.createStatement();
            String dbquery = "select id, last_run_no from index_job where name = '"
                    + jobName + "'";
            ResultSet rs = stmt.executeQuery(dbquery);
            rs.next();
            jobId = rs.getLong("id");
            runNo = rs.getLong("last_run_no");

            // publish run number used for this indexing job
            JobRunNos.setRunNo(jobName, runNo);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareStatements() {
        try {
            String update = "UPDATE indexed_file "
               + "  SET modified = ?, size = ?, last_indexed = ?, last_indexed_run = ?, deleted = false "
               + "WHERE job_id = ? AND url = ?";

            pstmtUpdate = conn.prepareStatement(update);

            String insert = "INSERT INTO indexed_file "
                    + "(job_id, url, modified, size, last_indexed, last_indexed_run) " 
                    + "VALUES (?,?,?,?,?,?)";

            pstmtInsert = conn.prepareStatement(insert);

            String delete = "UPDATE indexed_file "
                    + "  SET deleted = true, last_indexed = ?, last_indexed_run = ? "
                    + "WHERE job_id = ? AND url = ?";

            pstmtMarkDeleted = conn.prepareStatement(delete);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Timestamp currentTime() {
        return new Timestamp((new Date()).getTime());
    }

}
