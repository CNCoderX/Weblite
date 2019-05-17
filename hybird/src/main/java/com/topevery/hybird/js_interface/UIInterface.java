package com.topevery.hybird.js_interface;

import android.content.Context;

import com.topevery.hybird.plugin.ui.UIPlugin;

import org.xwalk.core.JavascriptInterface;

/**
 * @author wujie
 */
public class UIInterface {
    private UIPlugin mUIPlugin;

    public UIInterface(Context context) {
        mUIPlugin = new UIPlugin(context);
    }

    @JavascriptInterface
    public void showToast(String text) {
        mUIPlugin.showToast(text);
    }

    @JavascriptInterface
    public void showNotification(String options) {
        mUIPlugin.showNotification(options);
    }

    @JavascriptInterface
    public void removeNotification(int id) {
        mUIPlugin.removeNotification(id);
    }
}
