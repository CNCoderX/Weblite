package com.topevery.hybird.plugin.file;

import android.net.Uri;

import com.topevery.hybird.utils.Utils;

import java.io.File;
import java.util.Date;

/**
 * @author wujie
 */
public class FileInfo {
    public String name;
    public String parent;
    public String path;
    public boolean isDirectory;
    public String lastModified;
    public long length;
    public String access;

    public FileInfo() {
    }

    public FileInfo(File file) {
        name = file.getName();
        parent = Uri.fromFile(file.getParentFile()).toString();
        path = Uri.fromFile(file).toString();
        isDirectory = file.isDirectory();
        lastModified = getLastModifiedTime(file.lastModified());
        length = file.length();
        access = getFileAccess(file);
    }

    private String getLastModifiedTime(long time) {
        if (time > 0) {
            Date date = new Date(time);
            return Utils.formatDateTime(date);
        }
        return null;
    }

    private String getFileAccess(File file) {
        StringBuilder builder = new StringBuilder();
        if (file.canRead()) {
            builder.append("r");
        }
        if (file.canWrite()) {
            builder.append("w");
        }
        if (file.canExecute()) {
            builder.append("e");
        }
        return builder.toString();
    }
}
