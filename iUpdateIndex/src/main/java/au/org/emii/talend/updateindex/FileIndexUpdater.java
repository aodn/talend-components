package au.org.emii.talend.updateindex;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import paolo.test.custom_classloader.DirectoryBasedParentLastURLClassLoader;

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

    public FileIndexUpdater(Connection conn, String schemaName, String jobName, boolean liquibaseVersion4) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.conn = conn;
        this.jobName = jobName;
        this.schemaName = schemaName;

        runLiquibase4();
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
                System.err.println("* iUpdateIndex: WARNING - " + file.getRelativePath() + " not found on index - no action will be taken");
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
    private void runLiquibase4() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        File dir = new File(System.getProperty("user.dir") + "liquibase4");
        String JARS_DIR = dir.getAbsolutePath();

        System.out.println("Using " + JARS_DIR);
        // /var/lib/talend7/workspace/SOOP_CO2_RT/poms/jobs/process/soop_co2_rt_harvester_0.1/src/main/ext-resources/liquibase4
     // ls /var/lib/talend7/workspace/SOOP_CO2_RT/temp/lib

        DirectoryBasedParentLastURLClassLoader classLoader = new DirectoryBasedParentLastURLClassLoader(JARS_DIR);
        Class<?> classLiquibase = classLoader.loadClass("liquibase.Liquibase");
        Class<?> classDatabase = classLoader.loadClass("liquibase.database.Database");
        Class<?> classDatabaseFactory = classLoader.loadClass("liquibase.database.DatabaseFactory");
        Class<?> classJdbcConnection = classLoader.loadClass("liquibase.database.jvm.JdbcConnection");
        Class<?> classDatabaseConnection = classLoader.loadClass("liquibase.database.DatabaseConnection");
        Class<?> classClassLoaderResourceAccessor = classLoader.loadClass("liquibase.resource.ClassLoaderResourceAccessor");
        Class<?> classResourceAccessor = classLoader.loadClass("liquibase.resource.ResourceAccessor");

//        liquibase.database.jvm.JdbcConnection

        java.lang.reflect.Method databaseFactory_getInstance = classDatabaseFactory.getMethod("getInstance");
        java.lang.reflect.Method databaseFactory_findCorrectDatabaseImplementation = classDatabaseFactory.getMethod("findCorrectDatabaseImplementation", classDatabaseConnection );
        java.lang.reflect.Method database_setDefaultSchemaName = classDatabase.getMethod("setDefaultSchemaName", String.class);

        Method[] methods = classLiquibase.getDeclaredMethods();
        for(int i = 0; i < methods.length; i++) {
            System.out.println("The method is: " + methods[i].toString());
        }

        java.lang.reflect.Method liquibase_update = classLiquibase.getMethod("update");

        try {
//          Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Object databaseFactory = databaseFactory_getInstance.invoke(null);
            Object jdbcConnection = classJdbcConnection.getConstructor(java.sql.Connection.class).newInstance(conn);
            Object database = databaseFactory_findCorrectDatabaseImplementation.invoke(databaseFactory, jdbcConnection);

//          database.setDefaultSchemaName(schemaName);
            database_setDefaultSchemaName.invoke(database, schemaName);

//          Liquibase liquibase = new Liquibase("iPostgresqlIndexResources_changelog.xml", new ClassLoaderResourceAccessor(), database);
            Object classLoaderResourceAccessor = classClassLoaderResourceAccessor.getConstructor().newInstance();
            Object oLiquibase = classLiquibase.getConstructor(String.class, classResourceAccessor, classDatabase).newInstance("iPostgresqlIndexResources_changelog.xml", classLoaderResourceAccessor, database);

//          liquibase.update(null);
            liquibase_update.invoke(oLiquibase);

        } catch (InstantiationException e) {
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
