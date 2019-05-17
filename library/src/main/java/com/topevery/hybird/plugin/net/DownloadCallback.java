package com.topevery.hybird.plugin.net;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.topevery.hybird.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author wujie
 */
public abstract class DownloadCallback implements okhttp3.Callback {
    private File mDestDir;

    public DownloadCallback(File destDir) {
        this.mDestDir = destDir;
    }

    @Override
    public void onResponse(Call call, Response response) {
        new ResponseTask(this, call, response, mDestDir).execute();
    }

    public abstract void onResponse(Call call, Response response, File file);

    private static class TaskResult {
        public boolean success;
        public Object data;

        public TaskResult(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }
    }

    private static class ResponseTask extends AsyncTask<String, Integer, TaskResult> {
        final Call mCall;
        final Response mResponse;
        final File mFile;
        final WeakReference<DownloadCallback> mCallback;

        private ResponseTask(DownloadCallback callback, Call call, Response response, File destDir) {
            mCallback = new WeakReference<>(callback);
            mCall = call;
            mResponse = response;

            String fileName = parseFileName();
            File file = new File(destDir, fileName);
            mFile = Utils.uniqueFileName(file);
        }

        private String parseFileName() {
            Headers headers = mResponse.headers();
            if (headers != null) {
                String content = headers.get("Content-Disposition");
                if (!TextUtils.isEmpty(content)) {
                    String[] attributes = content.split(";");
                    for (String attribute : attributes) {
                        attribute = attribute.trim();
                        int index = attribute.indexOf("filename=");
                        if (index != -1) {
                            String filename = attribute.substring(index + "filename=".length());
                            return filename;
                        }
                    }
                }
            }
            return "untitled";
        }

        @Override
        protected TaskResult doInBackground(String... strings) {
            ResponseBody body = mResponse.body();
            if (body == null) {
                return new TaskResult(false, new IOException());
            }
            BufferedSource source = null;
            BufferedSink sink = null;
            try {
                source = body.source();
                sink = Okio.buffer(Okio.sink(mFile));
                sink.writeAll(source);
                sink.flush();
                return new TaskResult(true, null);
            } catch (IOException e) {
                return new TaskResult(false, e);
            } finally {
                Util.closeQuietly(source);
                Util.closeQuietly(sink);
            }
        }

        @Override
        protected void onPostExecute(TaskResult result) {
            DownloadCallback callback = mCallback.get();
            if (callback != null) {
                if (result.success) {
                    callback.onResponse(mCall, mResponse, mFile);
                } else {
                    callback.onFailure(mCall, (IOException) result.data);
                }
            }
        }
    }
}
