package com.topevery.hybird.js_interface;

import android.content.Context;

import com.topevery.hybird.plugin.net.NetworkPlugin;

import org.xwalk.core.JavascriptInterface;

/**
 * @author wujie
 */
public class NetworkInterface {
    private NetworkPlugin mNetworkPlugin;

    public NetworkInterface(Context context) {
        mNetworkPlugin = new NetworkPlugin(context);
    }

    @JavascriptInterface
    public void request(String url,
                        String method,
                        String data,
                        String headers,
                        int timeout,
                        int success,
                        int failure) {

        mNetworkPlugin.request(url, method, data, headers, timeout, success, failure);
    }

    @JavascriptInterface
    public void upload(String url,
                       String path,
                       String data,
                       String headers,
                       int timeout,
                       int success,
                       int failure) {

        mNetworkPlugin.upload(url, path, data, headers, timeout, success, failure);
    }

    @JavascriptInterface
    public void download(String url,
                         String dir,
                         String headers,
                         int timeout,
                         int success,
                         int failure) {

        mNetworkPlugin.download(url, dir, headers, timeout, success, failure);
    }

}
