package au.org.emii.talend.updateindex;

import java.io.File;
import java.util.Date;

public class FileResource {

    private final String basePath;
    private final String relativePath;
    private final File file;

    public FileResource(String basePath, String relativePath) {
        this.basePath = basePath;
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

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FileResource[")
            .append("basePath: ").append(basePath).append(", ")
            .append("relativePath: ").append(relativePath)
            .append("]");
        return builder.toString();
    }
}
