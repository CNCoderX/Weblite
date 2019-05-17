package com.topevery.um;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.topevery.um.entity.Settings;

import org.xwalk.core.XWalkPreferences;

/**
 * @author wujie
 */
public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, BuildConfig.DEBUG);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);

        String host = Settings.getServerHost(this);
        Intent intent = new Intent(this, WebActivity.class);
        intent.setData(Uri.parse("file:///android_asset/index.html?host=" + host));
//        intent.setData(Uri.parse("http://192.168.20.96/webwechat/mobile/zhifa/index.html"));
//        intent.setData(Uri.parse("http://www.baidu.com/"));
//        intent.setData(Uri.parse("http://192.168.20.215/WebWechat/mobile/case/app.html"));
        startActivity(intent);
        finish();
    }

    public LauncherManager getLauncherManager() {
        return new LauncherManager(this, getComponentName());
    }
}
