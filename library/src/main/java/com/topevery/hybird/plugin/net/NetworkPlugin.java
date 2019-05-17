package com.topevery.hybird.plugin.net;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.JsonUtils;
import com.topevery.hybird.utils.Storage;
import com.topevery.hybird.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author wujie
 */
public class NetworkPlugin {
    private final Context context;

    public NetworkPlugin(Context context) {
        this.context = context;
    }

    public void request(String url,
                        String method,
                        String data,
                        String headers,
                        int timeout,
                        int _success,
                        int _failure) {

        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        OkHttpClient httpClient;
        if (timeout == 0) {
            httpClient = new OkHttpClient.Builder().build();
        } else {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        }

        Request.Builder request = new Request.Builder();

        Map headerMap = JsonUtils.fromJson(headers, Map.class);
        if (headerMap != null) {
            request.headers(getHeaders(headerMap));
        }

        String contentType = getContentType(headerMap);
        boolean isUrlEncodedData = contentType != null &&
                contentType.equals("application/x-www-form-urlencoded");

        if (!URLUtil.isNetworkUrl(url)) {
            if (failure != null) {
                failure.invoke("invalid URL");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }
        if (method == null) {
            method = Method.GET;
        }

        try {
            if (isUrlEncodedData || Method.isGet(method)) {
                Map dataMap = JsonUtils.fromJson(data, Map.class);
                if (dataMap != null) {
                    url = getFullyUrl(url, dataMap);
                }
                request.url(url);
            } else {
                request.url(url);
            }
        } catch (IllegalArgumentException e) {
            if (failure != null) {
                failure.invoke("invalid url.");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }

        if (isUrlEncodedData || Method.isGet(method) || Method.isHead(method)) {
            request.method(method, null);
        } else {
            boolean isJsonData = contentType != null &&
                    contentType.equals("application/json");

            RequestBody requestBody = null;
            if (isJsonData) {
                if (data != null) {
                    requestBody = RequestBody.create(
                            MediaType.parse("application/json"), data);
                }
            } else {
                Map formMap = JsonUtils.fromJson(data, Map.class);
                if (formMap != null) {
                    requestBody = getRequestBody(formMap);
                }
            }

            try {
                request.method(method, requestBody);
            } catch (Exception e) {
                if (failure != null) {
                    failure.invoke(e.getMessage());
                    Memory.releaseObject(failure);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                return;
            }
        }

        final Call call = httpClient.newCall(request.build());
        call.enqueue(new StringCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (failure != null) {
                    failure.invoke(e.getMessage());
                    Memory.releaseObject(failure);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
            }

            @Override
            public void onResponse(Call call, Response response, String string) {
                int code = response.code();
                String headers = null;
                String data = Utils.encodeURI(string);

                Headers _headers = response.headers();
                if (_headers != null) {
                    headers = JsonUtils.toJson(getResponseHeaders(_headers));
                }

                if (success != null) {
                    success.invoke(code, headers, data);
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }

            Map getResponseHeaders(Headers headers) {
                int size = headers.size();
                Map map = new HashMap(size);
                for (int i = 0; i < size; i++) {
                    String name = headers.name(i);
                    String value = headers.value(i);
                    map.put(name, value);
                }
                return map;
            }
        });
    }

    private static String getContentType(Map headers) {
        if (headers == null || headers.isEmpty())
            return null;

        Object contentType = null;
        if (headers.containsKey("Content-Type")) {
            contentType = headers.get("Content-Type");
        }

        return contentType == null ? null : contentType.toString();
    }

    private static String getFullyUrl(String url, Map map) {
        boolean first = true;
        StringBuilder builder = new StringBuilder(url);
        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry entry : entries) {
            if (first) {
                first = false;
                builder.append('?');
            } else {
                builder.append('&');
            }
            String name = URLEncoder.encode(entry.getKey().toString());
            String value = URLEncoder.encode(entry.getValue().toString());
            builder.append(name).append('=').append(value);
        }
        return builder.toString();
    }

    private static Headers getHeaders(Map map) {
        HashMap<String, String> headers = new HashMap<>();
        Set<Map.Entry> entries = map.entrySet();
        for (Map.Entry entry : entries) {
            String name = entry.getKey().toString();
            String value = entry.getValue().toString();
            headers.put(name, value);
        }
        return Headers.of(headers);
    }

    private static RequestBody getRequestBody(Map map) {
        FormBody.Builder builder = new FormBody.Builder(Charset.defaultCharset());
        if (map != null && !map.isEmpty()) {
            Set<Map.Entry> entries = map.entrySet();
            for (Map.Entry entry : entries) {
                String name = entry.getKey().toString();
                String value = entry.getValue().toString();
                builder.add(name, value);
            }
        }
        return builder.build();
    }

    private static RequestBody getRequestBody(Map map, String contentType, File file) {
        RequestBody requestBody = null;
        if (file != null && file.exists()) {
            if (TextUtils.isEmpty(contentType)) {
                contentType = "application/octet-stream";
            }
            MediaType mediaType = MediaType.parse(contentType);
            requestBody = RequestBody.create(mediaType, file);
        }
        return requestBody;
    }

    public void upload(String url,
                       String path,
                       String data,
                       String headers,
                       int timeout,
                       int _success,
                       int _failure) {

        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        OkHttpClient httpClient;
        if (timeout == 0) {
            httpClient = new OkHttpClient.Builder().build();
        } else {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        }

        Request.Builder request = new Request.Builder();

        Map headerMap = JsonUtils.fromJson(headers, Map.class);
        if (headerMap != null) {
            request.headers(getHeaders(headerMap));
        }

        if (!URLUtil.isNetworkUrl(url)) {
            if (failure != null) {
                failure.invoke("invalid URL");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }
        request.url(url);

        if (!URLUtil.isFileUrl(path)) {
            if (failure != null) {
                failure.invoke("invalid file path");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }

        path = Uri.parse(path).getPath();
        File file = new File(path);
        if (!file.exists()) {
            if (failure != null) {
                failure.invoke("file not found.");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }

        Map formMap = JsonUtils.fromJson(data, Map.class);
        String contentType = getContentType(headerMap);
        RequestBody requestBody = getRequestBody(formMap, contentType, file);
        try {
            request.post(requestBody);
        } catch (Exception e) {
            if (failure != null) {
                failure.invoke(e.getMessage());
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }

        final Call call = httpClient.newCall(request.build());
        call.enqueue(new StringCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (failure != null) {
                    failure.invoke(e.getMessage());
                    Memory.releaseObject(failure);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
            }

            @Override
            public void onResponse(Call call, Response response, String string) {
                int code = response.code();
                String headers = null;
                String data = URLEncoder.encode(string);

                Headers _headers = response.headers();
                if (_headers != null) {
                    headers = JsonUtils.toJson(getResponseHeaders(_headers));
                }

                if (success != null) {
                    success.invoke(code, headers, data);
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        });
    }

    public void download(String url,
                         String dir,
                         String headers,
                         int timeout,
                         int _success,
                         int _failure) {

        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        OkHttpClient httpClient;
        if (timeout == 0) {
            httpClient = new OkHttpClient.Builder().build();
        } else {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        }

        Request.Builder request = new Request.Builder();

        Map headerMap = JsonUtils.fromJson(headers, Map.class);
        if (headerMap != null) {
            request.headers(getHeaders(headerMap));
        }

        if (!URLUtil.isNetworkUrl(url)) {
            if (failure != null) {
                failure.invoke("invalid URL");
                Memory.releaseObject(failure);
            }
            if (success != null) {
                Memory.releaseObject(success);
            }
            return;
        }
        request.url(url);
        request.get();

        final File destination;
        if (dir == null) {
            destination = Storage.getDownloadDir(context);
        } else {
            destination = new File(dir);
        }
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                if (failure != null) {
                    failure.invoke("Failed to create destination directory.");
                    Memory.releaseObject(failure);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                return;
            }
        }
        final Call call = httpClient.newCall(request.build());
        call.enqueue(new DownloadCallback(destination) {
            @Override
            public void onFailure(Call call, IOException e) {
                if (failure != null) {
                    failure.invoke(e.getMessage());
                    Memory.releaseObject(failure);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
            }

            @Override
            public void onResponse(Call call, Response response, File file) {
                int code = response.code();
                String headers = null;

                Headers _headers = response.headers();
                if (_headers != null) {
                    headers = JsonUtils.toJson(getResponseHeaders(_headers));
                }

                Uri uri = Uri.fromFile(file);

                if (success != null) {
                    success.invoke(code, headers, uri.toString());
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        });
    }

    private static Map getResponseHeaders(Headers headers) {
        int size = headers.size();
        Map map = new HashMap(size);
        for (int i = 0; i < size; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            map.put(name, value);
        }
        return map;
    }
}
