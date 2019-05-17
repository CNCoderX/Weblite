package com.topevery.hybird.js_interface;

import android.annotation.SuppressLint;
import android.content.Context;

import com.topevery.hybird.plugin.file.FilePlugin;

import org.xwalk.core.JavascriptInterface;

/**
 * @author wujie
 */
public class FileInterface {
    private FilePlugin mFilePlugin;

    public FileInterface(Context context) {
        mFilePlugin = new FilePlugin(context);
    }

    @JavascriptInterface
    public String getFileInfo(String path) {
        return mFilePlugin.getFileInfo(path);
    }

    @JavascriptInterface
    public void chooseFile(String accept, int success, int failure) {
        mFilePlugin.chooseFile(accept, success, failure);
    }

    @JavascriptInterface
    public boolean fileExists(String path) {
        return mFilePlugin.fileExists(path);
    }

    @JavascriptInterface
    public String createFile(String path) {
        return mFilePlugin.createFile(path);
    }

    @JavascriptInterface
    public String createDirectory(String path) {
        return mFilePlugin.createDirectory(path);
    }

    @JavascriptInterface
    public boolean deleteFile(String path) {
        return mFilePlugin.deleteFile(path);
    }

    @JavascriptInterface
    public boolean deleteDirectory(String path) {
        return mFilePlugin.deleteDirectory(path);
    }

    @JavascriptInterface
    public String listFiles(String path) {
        return mFilePlugin.listFiles(path);
    }

    @JavascriptInterface
    public boolean renameFile(String path, String newName) {
        return mFilePlugin.renameFile(path, newName);
    }

    @SuppressLint("StaticFieldLeak")
    @JavascriptInterface
    public void copyFile(String src, String dest, int success, int failure) {
        mFilePlugin.copyFile(src, dest, success, failure);
    }

    @JavascriptInterface
    public void writeStringToFile(String url, String text, boolean append, int success, int failure) {
        mFilePlugin.writeStringToFile(url, text, append, success, failure);
    }

    @JavascriptInterface
    public void readStringFromFile(String url, int offset, int length, int success, int failure) {
        mFilePlugin.readStringFromFile(url, offset, length, success, failure);
    }

    @JavascriptInterface
    public String getCacheDir() {
        return mFilePlugin.getCacheDir();
    }

    @JavascriptInterface
    public String getFilesDir() {
        return mFilePlugin.getFilesDir();
    }

    @JavascriptInterface
    public String getExternalCacheDir() {
        return mFilePlugin.getExternalCacheDir();
    }

    @JavascriptInterface
    public String getExternalFilesDir(String type) {
        return mFilePlugin.getExternalFilesDir(type);
    }

    @JavascriptInterface
    public String getExternalStorageDir(String type) {
        return mFilePlugin.getExternalStorageDir(type);
    }

    @JavascriptInterface
    public int Preferences_create(final String name) {
        return mFilePlugin.Preferences_create(name);
    }

    @JavascriptInterface
    public boolean Preferences_contains(int pointer, String key) {
        return mFilePlugin.Preferences_contains(pointer, key);
    }

    @JavascriptInterface
    public String Preferences_get(int pointer, String key) {
//        return mFilePlugin.Preferences_get(pointer, key);
        return mFilePlugin.Preferences_get(pointer, key);
    }

    @JavascriptInterface
    public boolean Preferences_set(int pointer, String key, String value) {
        return mFilePlugin.Preferences_set(pointer, key, value);
    }

    @JavascriptInterface
    public boolean Preferences_remove(int pointer, String key) {
        return mFilePlugin.Preferences_remove(pointer, key);
    }

    @JavascriptInterface
    public boolean Preferences_clear(int pointer) {
        return mFilePlugin.Preferences_clear(pointer);
    }

    @JavascriptInterface
    public void Preferences_release(int pointer) {
        mFilePlugin.Preferences_release(pointer);
    }
}
