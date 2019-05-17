package com.topevery.hybird;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.topevery.hybird.js_interface.AppInterface;
import com.topevery.hybird.js_interface.DeviceInterface;
import com.topevery.hybird.js_interface.FileInterface;
import com.topevery.hybird.js_interface.MediaInterface;
import com.topevery.hybird.js_interface.NetworkInterface;
import com.topevery.hybird.js_interface.UIInterface;
import com.topevery.hybird.plugin.device.CaptureBitmapCallback;
import com.topevery.hybird.reflect.Memory;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkGetBitmapCallback;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author wujie
 */
public abstract class HybirdActivity extends Activity {
    private XWalkView xWalkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybird);
        xWalkView = findViewById(R.id.xwalk_view);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        xWalkView.setUIClient(new XWalkUIClient(xWalkView));
        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView) {
            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
                Log.i("hybird", "Url loading: " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        addDefaultJavascriptInterface();
        onCreateReady(new WebSettings(xWalkView.getSettings()));
    }

    public abstract void onCreateReady(WebSettings settings);

    private void addDefaultJavascriptInterface() {
        xWalkView.addJavascriptInterface(new JsFunctionInterface(this), "android_js_func");
        xWalkView.addJavascriptInterface(new AppInterface(this), "android_app");
        xWalkView.addJavascriptInterface(new DeviceInterface(this), "android_device");
        xWalkView.addJavascriptInterface(new MediaInterface(this), "android_media");
        xWalkView.addJavascriptInterface(new FileInterface(this), "android_file");
        xWalkView.addJavascriptInterface(new NetworkInterface(this), "android_net");
        xWalkView.addJavascriptInterface(new UIInterface(this), "android_ui");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (xWalkView != null) {
            xWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (xWalkView != null) {
            xWalkView.onNewIntent(intent);
        }
    }

    public void addJavascriptInterface(Object object, String name) {
        xWalkView.addJavascriptInterface(object, name);
    }

    public void removeJavascriptInterface(String name) {
        xWalkView.removeJavascriptInterface(name);
    }

    public void loadData(String data, String mimeType, String encoding) {
        xWalkView.loadData(data, mimeType, encoding);
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        xWalkView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void loadUrl(String url) {
        xWalkView.loadUrl(url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        xWalkView.loadUrl(url, additionalHttpHeaders);
    }

    public void loadAppFromManifest(String url, String content) {
        xWalkView.loadAppFromManifest(url, content);
    }

    public void setUIClient(XWalkUIClient uiClient) {
        xWalkView.setUIClient(uiClient);
    }

    public void setResourceClient(XWalkResourceClient resourceClient) {
        xWalkView.setResourceClient(resourceClient);
    }

    @Override
    public void onBackPressed() {
        if(xWalkView.getNavigationHistory().canGoBack()) {
            xWalkView.getNavigationHistory().navigate(
                    XWalkNavigationHistory.Direction.BACKWARD, 1);
        } else {
            super.onBackPressed();
        }
    }

    private static class JsFunctionInterface {
        WeakReference<HybirdActivity> mHybirdActivity;

        public JsFunctionInterface(HybirdActivity hybirdActivity) {
            mHybirdActivity = new WeakReference<>(hybirdActivity);
        }

        @JavascriptInterface
        public int createJsFunction() {
            HybirdActivity hybirdActivity = mHybirdActivity.get();
            if (hybirdActivity == null) {
                return Memory.NULL;
            } else {
                return Memory.createObject(new JsFunctionFactory(hybirdActivity));
            }
        }
    }

    public void evaluateJavascript(final String script) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (xWalkView != null) {
                xWalkView.evaluateJavascript("javascript:" + script, null);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    evaluateJavascript(script);
                }
            });
        }
    }

    public void captureBitmap(final CaptureBitmapCallback callback) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            xWalkView.captureBitmapAsync(new XWalkGetBitmapCallback() {
                @Override
                public void onFinishGetBitmap(Bitmap bitmap, int i) {
                    callback.getBitmap(bitmap);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    captureBitmap(callback);
                }
            });
        }
    }

}
