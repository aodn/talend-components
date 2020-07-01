package au.org.emii.talend.updateindex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class Component {

    private static int COMMIT_EVERY = 10000;

    public Results run(Parameters params) throws Exception {
        Results results = new Results();

        Connection conn = null;

        try {
            // Connect to index schema
            String url = "jdbc:postgresql://" + params.dbhost + ":" + params.dbport + "/" + params.dbname;

            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, params.userName, params.password);
            conn.setAutoCommit(false);

            FileIndexUpdater fileIndexUpdater = new FileIndexUpdater(
                conn, params.dbschema, params.jobName
            );

            BufferedReader br = new BufferedReader(new FileReader(params.manifestFilename ));

            try {
                String relativePath = null;

                while ((relativePath = br.readLine()) != null && !relativePath.isEmpty()) {
                    FileResource file = new FileResource(
                        params.basePath, relativePath
                    );

                    if (file.exists()) {
                        System.out.println("* iUpdateIndex: " + file + " is new or has been modified");
                        fileIndexUpdater.addOrUpdateFile(file);
                    } else {
                        System.out.println("* iUpdateIndex: " + file + " has been deleted");
                        fileIndexUpdater.deleteFile(file);
                    }

                    if (fileIndexUpdater.getProcessedFileCount() % COMMIT_EVERY == 0) {
                        conn.commit();
                    }
                }
            } finally {
                br.close();
            }

            conn.commit();

            results.processedFileCount = fileIndexUpdater.getProcessedFileCount();
            return results;
        } finally {
            // ensure connection is always closed
            if (conn != null) {
                conn.rollback();
                conn.close();
                conn = null;
            }
        }
    }
}
