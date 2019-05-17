package com.topevery.um;

import com.topevery.hybird.HybirdActivity;
import com.topevery.hybird.WebSettings;
import com.topevery.um.entity.Settings;
import com.umeng.message.PushAgent;

import org.xwalk.core.XWalkPreferences;

public class WebActivity extends HybirdActivity {

    @Override
    public void onCreateReady(WebSettings settings) {
        if (BuildConfig.DEBUG) {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, BuildConfig.DEBUG);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);

        addJavascriptInterface(new ExtraInterface(this), "android_extra");
        addJavascriptInterface(new Object(), "Android"); // 给前端判断当前设备为android设备

//        String host = "192.168.20.91";
        String host = Settings.getServerHost(this);
        loadUrl("file:///android_asset/main/index.html?host=" + host);

//        loadUrl("http://192.168.20.74/debbug/main/index.html");
//        loadUrl("http://192.168.20.96/webwechat/mobile/zhifa/app.html");

//        loadUrl("file:///android_asset/media_choose_images.html");
//        loadUrl("http://192.168.20.91/andhub/login.html");
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushAgent pushAgent = PushAgent.getInstance(getApplicationContext());
        pushAgent.onAppStart();
    }
}
