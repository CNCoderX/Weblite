package com.topevery.um;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.webkit.URLUtil;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topevery.hybird.plugin.device.LBSLocation;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.JsonUtils;
import com.topevery.um.entity.Authorizations;
import com.topevery.um.entity.Profile;
import com.topevery.um.entity.Settings;
import com.topevery.um.utils.UserInfoSerializer;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;

import org.xwalk.core.JavascriptInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.co.canon.bsd.ad.pixmaprint.a.PixmaPrint;

/**
 * @author wujie
 */
public class ExtraInterface {
    private Context context;

    public ExtraInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void exit() {
        ((Activity) context).finish();
    }

    @JavascriptInterface
    public String getHost() {
        return Settings.getServerHost(context);
    }

    @JavascriptInterface
    public String getUserInfo() {
        Context context = this.context.getApplicationContext();
        Authorizations authorizations = Authorizations.getInstance(context);
        Profile profile = authorizations.getProfile();

        Gson gson = new GsonBuilder().registerTypeAdapter(
                Profile.class, new UserInfoSerializer()).create();

        return profile == null ? null : gson.toJson(profile);
    }

    @JavascriptInterface
    public void getBDLocation(int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(800);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);
        final LocationClient locationClient = new LocationClient(context, option);
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location != null) {
                    if (success != null) {
                        LBSLocation lbsLocation = new LBSLocation();
                        lbsLocation.latitude = location.getLatitude();
                        lbsLocation.longitude = location.getLongitude();
                        lbsLocation.speed = location.getSpeed();
                        lbsLocation.accuracy = location.getRadius();
                        lbsLocation.altitude = location.getAltitude();
                        lbsLocation.provider = location.getLocTypeDescription();
                        lbsLocation.time = location.getTime();
                        success.invoke(JsonUtils.toJson(lbsLocation));
                    }
                } else {
                    if (failure != null) {
                        failure.invoke("location == null");
                    }
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }

                locationClient.unRegisterLocationListener(this);
                locationClient.stop();
            }
        });
        locationClient.start();
    }

    @JavascriptInterface
    public boolean canonPrint(String url) {
        if (URLUtil.isFileUrl(url)) {
            String path = Uri.parse(url).getPath();
            return PixmaPrint.printLocalDocument(context, path);
        }

        if (URLUtil.isNetworkUrl(url)) {
            return PixmaPrint.printRemoteDocument(context, url);
        }

        return false;
    }

    @JavascriptInterface
    public void enableUPush(int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        Context context = this.context.getApplicationContext();
        PushAgent pushAgent = PushAgent.getInstance(context);
        pushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                if (success != null) {
                    success.invoke();
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }

            @Override
            public void onFailure(String errCode, String errMsg) {
                if (failure != null) {
                    failure.invoke(errMsg);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        });
    }

    @JavascriptInterface
    public void disableUPush(int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        Context context = this.context.getApplicationContext();
        PushAgent pushAgent = PushAgent.getInstance(context);
        pushAgent.disable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                if (success != null) {
                    success.invoke();
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }

            @Override
            public void onFailure(String errCode, String errMsg) {
                if (failure != null) {
                    failure.invoke(errMsg);
                }
                if (success != null) {
                    Memory.releaseObject(success);
                }
                if (failure != null) {
                    Memory.releaseObject(failure);
                }
            }
        });
    }

    @JavascriptInterface
    public int registerUPushHandler(int handler) {
        UMPushMessageService.setHandler(handler);
        return handler;
    }

    @JavascriptInterface
    public void unregisterUPushHandler(int handler) {
        Memory.releaseObject(handler);
    }

    @JavascriptInterface
    public void queryUPushMessage(final int index, final int size, final int _result) {
        Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(SingleEmitter<List<String>> emitter) throws Exception {
                SQLiteDatabase db = UPushDBHelper.getInstance(context).getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM UMENG_MSG ORDER BY TIME DESC LIMIT ? OFFSET ?",
                        new String[] {String.valueOf(size), String.valueOf(index * size)});
                try {
                    List<String> messages = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        String message = cursor.getString(1);
                        messages.add(message);
                    }
                    emitter.onSuccess(messages);
                } finally {
                    cursor.close();
                    db.close();
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> messages) throws Exception {
                JsFunction result = (JsFunction) Memory.getObject(_result);
                if (result != null) {
                    result.invoke(JsonUtils.toJson(messages));
                }
            }
        });
    }
}
