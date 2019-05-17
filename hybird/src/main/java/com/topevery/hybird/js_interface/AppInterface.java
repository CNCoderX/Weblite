package com.topevery.hybird.js_interface;

import android.content.Context;

import com.topevery.hybird.plugin.app.AppPlugin;

import org.xwalk.core.JavascriptInterface;

/**
 * @author wujie
 */
public class AppInterface {
    private AppPlugin mAppPlugin;

    public AppInterface(Context context) {
        mAppPlugin = new AppPlugin(context);
    }

    @JavascriptInterface
    public String getPackageInfo() {
        return mAppPlugin.getPackageInfo();
    }

    @JavascriptInterface
    public void installApp(String url) {
        mAppPlugin.installApp(url);
    }

    @JavascriptInterface
    public void uninstallApp(String packageName) {
        mAppPlugin.uninstallApp(packageName);
    }

    @JavascriptInterface
    public boolean appInstalled(String packageName) {
        return mAppPlugin.appInstalled(packageName);
    }

    @JavascriptInterface
    public boolean launchApp(String packageName, String className, String data) {
        return mAppPlugin.launchApp(packageName, className, data);
    }
}
