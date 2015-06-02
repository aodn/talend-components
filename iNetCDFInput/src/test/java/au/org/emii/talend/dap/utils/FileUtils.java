package au.org.emii.talend.dap.utils;

import java.io.IOException;

import ucar.nc2.util.cache.FileCacheable;

public class FileUtils {

    public static void closeQuietly(FileCacheable cacheableFile) {
        if (cacheableFile != null) {
             try {
                cacheableFile.close();
            } catch (IOException e) {
                // QUIET!
            }
        }
    }

}
