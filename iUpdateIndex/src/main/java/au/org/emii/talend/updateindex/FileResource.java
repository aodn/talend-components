package au.org.emii.talend.updateindex;

import java.io.File;
import java.util.Date;

public class FileResource {

    private String relativePath;
    private File file;

    public FileResource(String basePath, String relativePath) {
        this.relativePath = relativePath;
        this.file = new File(basePath, relativePath);
    }

    public boolean exists() {
        return file.exists();
    }

    public double getSize() {
        return file.length();
    }

    public String getRelativePath() {
        return relativePath;
    }

    public Date getLastModified() {
        return new Date(file.lastModified());
    }
}
