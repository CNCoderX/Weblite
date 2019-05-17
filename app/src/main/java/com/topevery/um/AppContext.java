package com.topevery.um;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * @author wujie
 */
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        initUMConfigure();
//        registerPushAgent();
    }

    private void initUMConfigure() {
        String appKey = "";
        String secret = "";
        String channel = "";

        try {
            String pkgName = getPackageName();
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
            appKey = appInfo.metaData.getString("UMENG_APPKEY");
            secret = appInfo.metaData.getString("UMENG_MESSAGE_SECRET");
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        UMConfigure.init(this, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, secret);
//        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
    }

    private void registerPushAgent() {
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String token) {
                Log.i("UMENG", "Device token:" + token);
            }

            @Override
            public void onFailure(String errCode, String errMsg) {

            }
        });
        pushAgent.setPushIntentServiceClass(UMPushMessageService.class);
    }
}
