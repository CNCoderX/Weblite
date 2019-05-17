package com.topevery.hybird.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author wujie
 */
public class Storage {

    public static File getMediaDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }

    public static File getPictureDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static File getDownloadDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    }

    public static File getCameraDir(Context context) {
        File parent = getMediaDir(context);
        File file = new File(parent, "Camera");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getRecordDir(Context context) {
        File parent = getMediaDir(context);
        File file = new File(parent, "Record");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSnapshotDir(Context context) {
        File parent = getMediaDir(context);
        File file = new File(parent, "Snapshot");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getTempDir(Context context) {
        return context.getExternalFilesDir("Temp");
    }
}
