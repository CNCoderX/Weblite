package com.topevery.hybird.plugin.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.JsObject;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.JsonUtils;
import com.topevery.hybird.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wujie
 */
public class FilePlugin {
    private final Context context;

    public FilePlugin(Context context) {
        this.context = context;
    }

    public String getFileInfo(String url) {
        if (!URLUtil.isFileUrl(url))
            return null;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        if (file.exists()) {
            return JsonUtils.toJson(new FileInfo(file));
        }
        return null;
    }

    public void chooseFile(String accept, int _success, int _failure) {
        DummyResultFragment fragment = new ChooseFileResultFragment(accept, _success, _failure);
        fragment.show(context, "choose_file");
    }

    public boolean fileExists(String url) {
        if (!URLUtil.isFileUrl(url))
            return false;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        return file.exists();
    }

    public String createFile(String url) {
        if (!URLUtil.isFileUrl(url))
            return null;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                return JsonUtils.toJson(new FileInfo(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createDirectory(String url) {
        if (!URLUtil.isFileUrl(url))
            return null;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        if (file.mkdirs()) {
            return JsonUtils.toJson(new FileInfo(file));
        }
        return null;
    }

    public boolean deleteFile(String url) {
        if (!URLUtil.isFileUrl(url))
            return false;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        return file.delete();
    }

    public boolean deleteDirectory(String url) {
        if (!URLUtil.isFileUrl(url))
            return false;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            clearDirectory(file);
            return file.delete();
        }
        return false;
    }

    private void clearDirectory(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                clearDirectory(f);
                f.delete();
            } else {
                f.delete();
            }
        }
    }

    public String listFiles(String url) {
        if (!URLUtil.isFileUrl(url))
            return null;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            int length = files.length;
            FileInfo[] fileInfos = new FileInfo[length];
            for (int i = 0; i < length; i++) {
                fileInfos[i] = new FileInfo(files[i]);
            }
            return JsonUtils.toJson(fileInfos);
        }
        return null;
    }

    public boolean renameFile(String url, String newName) {
        if (!URLUtil.isFileUrl(url))
            return false;

        if (TextUtils.isEmpty(newName))
            return false;

        String path = Uri.parse(url).getPath();
        File file = new File(path);
        if (file.exists()) {
            String parent = file.getParent();
            return file.renameTo(new File(parent, newName));
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public void copyFile(final String src, final String dest, int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                FileInputStream input = null;
                FileOutputStream output = null;
                try {
                    if (URLUtil.isFileUrl(src) && URLUtil.isFileUrl(dest)) {
                        String srcPath = Uri.parse(src).getPath();
                        String destPath = Uri.parse(dest).getPath();

                        input = new FileInputStream(srcPath);
                        output = new FileOutputStream(destPath);
                        FileChannel inChannel = input.getChannel();
                        FileChannel outChannel = output.getChannel();

                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        emitter.onNext("success");
                    } else {
                        emitter.onError(new IllegalArgumentException("Invalid parameter."));
                    }
                } catch (IOException e) {
                    emitter.onError(e);
                } finally {
                    Utils.closeSilently(input);
                    Utils.closeSilently(output);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        })
        .subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                if (success != null) {
                    success.invoke();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (failure != null) {
                    failure.invoke(throwable.getMessage());
                }
            }
        });
    }

    public void writeStringToFile(final String url, final String text, final boolean append, int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                FileWriter writer = null;
                try {
                    if (!URLUtil.isFileUrl(url)) {
                        emitter.onError(new IllegalArgumentException("Invalid path."));
                        return;
                    }
                    if (text == null) {
                        emitter.onError(new IllegalArgumentException("Empty content."));
                        return;
                    }

                    String path = Uri.parse(url).getPath();
                    writer = new FileWriter(path, append);
                    writer.write(Utils.decodeURI(text));
                    writer.flush();

                    emitter.onNext("success");
                } catch (IOException e) {
                    emitter.onError(e);
                } finally {
                    Utils.closeSilently(writer);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        })
        .subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                if (success != null) {
                    success.invoke();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (failure != null) {
                    failure.invoke(throwable.getMessage());
                }
            }
        });
    }

    public void readStringFromFile(final String url, final int offset, final int length, int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                FileReader reader = null;
                try {
                    if (!URLUtil.isFileUrl(url)) {
                        emitter.onError(new IllegalArgumentException("Invalid path."));
                        return;
                    }

                    String path = Uri.parse(url).getPath();
                    int size = Math.min(Integer.MAX_VALUE - 2, length);
                    char[] chars = new char[size];
                    String content = null;

                    reader = new FileReader(path);
                    int length2 = reader.read(chars, offset, length);
                    if (length2 > 0) {
                        content = new String(chars, 0, length2);
                        content = Utils.encodeURI(content);
                        emitter.onNext(content);
                    } else {
                        emitter.onNext("");
                    }
                } catch (IOException e) {
                    emitter.onError(e);
                } finally {
                    Utils.closeSilently(reader);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        })
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String text) throws Exception {
                if (success != null) {
                    success.invoke(text);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (failure != null) {
                    failure.invoke(throwable.getMessage());
                }
            }
        });
    }

    public String getCacheDir() {
        File dir = context.getCacheDir();
        String url = Uri.fromFile(dir).toString();
        return url;
    }

    public String getFilesDir() {
        File dir = context.getFilesDir();
        String url = Uri.fromFile(dir).toString();
        return url;
    }

    public String getExternalCacheDir() {
        File file = context.getExternalCacheDir();
        if (file != null) {
            String url = Uri.fromFile(file).toString();
            return url;
        }
        return null;
    }

    public String getExternalFilesDir(String type) {
        File file = context.getExternalFilesDir(type);
        if (file != null) {
            String url = Uri.fromFile(file).toString();
            return url;
        }
        return null;
    }

    public String getExternalStorageDir(String type) {
        File dir;
        if (TextUtils.isEmpty(type)) {
            dir = Environment.getExternalStorageDirectory();
        } else {
            dir = Environment.getExternalStoragePublicDirectory(type);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String url = Uri.fromFile(dir).toString();
        return url;
    }

    public int Preferences_create(final String name) {
        if (TextUtils.isEmpty(name)) {
            return Memory.NULL;
        }

        return Memory.createObject(new Memory.JsObjectFactory() {
            @Override
            public JsObject create() {
                return new Preferences(context, name);
            }
        });
    }

    public boolean Preferences_contains(int pointer, String key) {
        if (TextUtils.isEmpty(key)) return false;

        Preferences preferences = (Preferences) Memory.getObject(pointer);
        return preferences != null && preferences.contains(key);
    }

    public String Preferences_get(int pointer, String key) {
        if (TextUtils.isEmpty(key)) return null;
        Preferences preferences = (Preferences) Memory.getObject(pointer);
        if (preferences == null) return null;
        try {
            return preferences.get(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean Preferences_set(int pointer, String key, String value) {
        if (TextUtils.isEmpty(key)) return false;

        Preferences preferences = (Preferences) Memory.getObject(pointer);
        return preferences != null && preferences.set(key, value);

    }

    public boolean Preferences_remove(int pointer, String key) {
        if (TextUtils.isEmpty(key)) return false;

        Preferences preferences = (Preferences) Memory.getObject(pointer);
        return preferences != null && preferences.remove(key);

    }

    public boolean Preferences_clear(int pointer) {

        Preferences preferences = (Preferences) Memory.getObject(pointer);
        return preferences != null && preferences.clear();

    }

    public void Preferences_release(int pointer) {
        Memory.releaseObject(pointer);
    }
}
