package com.topevery.hybird.plugin.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.RequiresPermission;
import android.support.v4.print.PrintHelper;
import android.telephony.TelephonyManager;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.JsonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * @author wujie
 */
public class DevicePlugin {
    private final Context context;

    public DevicePlugin(Context context) {
        this.context = context;
    }

    public String getDeviceInfo() {
        DeviceInfo devInfo = new DeviceInfo();
        return JsonUtils.toJson(devInfo);
    }

    public String getIMEI() {
        DeviceInfo devInfo = new DeviceInfo();
        return devInfo.getIMEI(context);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public int getNetworkType() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return 0; // none
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1; // wifi
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            // TD-SCDMA   networkType is 17
            int subType = networkInfo.getSubtype();
            String subTypeName = networkInfo.getSubtypeName();
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return 2; // 2g
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return 3; // 3g
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return 4; // 4g
                default:
                    // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                    if (subTypeName.equalsIgnoreCase("TD-SCDMA")
                            || subTypeName.equalsIgnoreCase("WCDMA")
                            || subTypeName.equalsIgnoreCase("CDMA2000")) {
                        return 3; // 3g
                    }
            }
        }
        return 0;
    }

    @RequiresPermission(anyOf = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION})
    public void getLocation(int _success, int _failure) {
        final JsFunction success = (JsFunction) Memory.getObject(_success);
        final JsFunction failure = (JsFunction) Memory.getObject(_failure);

        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            if (success != null) {
                Memory.releaseObject(success);
            }
            if (failure != null) {
                Memory.releaseObject(failure);
            }
            throw new RuntimeException();
        }

        LocationListener listener = new LBSLocationAdapter() {

            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    if (success != null) {
                        success.invoke(JsonUtils.toJson(new LBSLocation(location)));
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
            }
        };

        boolean isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled) {
            manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, Looper.myLooper());
        }

        boolean isNetEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetEnabled) {
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.myLooper());
        }
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    public void makePhoneCall(String phoneNumber) {
        if (phoneNumber != null) {
            Uri data = Uri.parse("tel:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_CALL, data);
            context.startActivity(intent);
        }
    }

    public void makeSMS(String phoneNumber, String message) {
        if (phoneNumber != null) {
            Uri data = Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, data);
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        }
    }

    public void setClipboardData(String data) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }
        if (data != null) {
            ClipData clipData = ClipData.newPlainText("label", data);
            manager.setPrimaryClip(clipData);
        }
    }

    public String getClipboardData() {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }
        if (manager.hasPrimaryClip()) {
            ClipData clipData = manager.getPrimaryClip();
            ClipData.Item clipDataItem = clipData.getItemAt(0);
            return clipDataItem.getText().toString();
        }
        return null;
    }

    public void captureScreen(int _success, int _failure) {
//        final JsFunction success = (JsFunction) Memory.getObject(_success);
//        final JsFunction failure = (JsFunction) Memory.getObject(_failure);
//
//        if (context instanceof JsBridge) {
//            (context).captureBitmap(new CaptureBitmapCallback() {
//                @Override
//                public void getBitmap(Bitmap bitmap) {
//                    @SuppressLint("SimpleDateFormat")
//                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                    String name = "IMG_" + dateFormat.format(new Date()) + ".jpg";
//                    File parent = Storage.getSnapshotDir(context);
//                    File file = new File(parent, name);
//                    try {
//                        FileOutputStream stream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        stream.close();
//
//                        if (success != null) {
//                            Uri uri = Uri.fromFile(file);
//                            success.invoke(uri.toString());
//                        }
//                    } catch (IOException e) {
//                        if (failure != null) {
//                            failure.invoke(e.getMessage());
//                        }
//                    } finally {
//                        if (success != null) {
//                            Memory.releaseObject(success);
//                        }
//                        if (failure != null) {
//                            Memory.releaseObject(failure);
//                        }
//                    }
//                }
//            });
//        }
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    public void vibrate(int duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            throw new RuntimeException();
        }
        vibrator.vibrate(duration);
    }

    public void printDocument(String name, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager manager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
            if (manager == null) {
                throw new RuntimeException();
            }
            if (URLUtil.isFileUrl(url)) {
                String path = Uri.parse(url).getPath();
                File file = new File(path);
                if (file.exists()) {
                    PrintDocumentAdapter printAdapter = new PrintPDFDocumentAdapter(name, path);
                    manager.print(name, printAdapter, new PrintAttributes.Builder().build());
                }
            }
        }
    }

    public void printPicture(String name, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (URLUtil.isFileUrl(url)) {
                String path = Uri.parse(url).getPath();
                File file = new File(path);
                if (file.exists()) {
                    try {
                        PrintHelper printHelper = new PrintHelper(context);
                        printHelper.printBitmap(name, Uri.fromFile(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void scanCode(String type, int success, int failure) {
        DummyResultFragment fragment = new ScanCodeResultFragment(type, success, failure);
        fragment.show(context, "scan_code");
    }

    @RequiresPermission(android.Manifest.permission.CHANGE_WIFI_STATE)
    public boolean openWifi() {
        Context c = context.getApplicationContext();
        WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }
        return manager.isWifiEnabled() || manager.setWifiEnabled(true);
    }

    @RequiresPermission(android.Manifest.permission.CHANGE_WIFI_STATE)
    public boolean closeWifi() {
        Context c = context.getApplicationContext();
        WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }
        return !manager.isWifiEnabled() || manager.setWifiEnabled(false);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_WIFI_STATE)
    public String getWifiList() {
        Context c = context.getApplicationContext();
        WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }

        List<WifiInfo> wifiInfos = new ArrayList<>();

        List<ScanResult> results = manager.getScanResults();
        for (ScanResult result : results) {
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.SSID = result.SSID;
            wifiInfo.BSSID = result.BSSID;
            wifiInfo.signalLevel = result.level;
            wifiInfo.cipherType = WifiCipher.getCipherType(result.capabilities);
            wifiInfos.add(wifiInfo);
        }

        return JsonUtils.toJson(wifiInfos);
    }

    @RequiresPermission(anyOf = {
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE})
    public boolean connectWifi(String SSID, String password, String cipherType) {
        Context c = context.getApplicationContext();
        WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            if (configuredNetwork.SSID != null && configuredNetwork.SSID.equals("\"" + SSID + "\"")) {
                return manager.enableNetwork(configuredNetwork.networkId, true);
            }
        }

        WifiConfiguration configuration = new WifiConfiguration();
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();
        configuration.SSID = "\"" + SSID + "\"";

        if (cipherType == null) {
            cipherType = WifiCipher.WPA;
        }
        if (cipherType.equals(WifiCipher.NONE)) {
            configuration.wepTxKeyIndex = 0;
            configuration.wepKeys[0] = "";
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (cipherType.equals(WifiCipher.WEP)) {
            configuration.wepTxKeyIndex = 0;
            configuration.hiddenSSID = false;
            configuration.wepKeys[0] = "\"" + password +"\"";
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            configuration.preSharedKey = "\"" + password + "\"";
            configuration.hiddenSSID = false;
            // configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            configuration.status = WifiConfiguration.Status.ENABLED;
        }

        int networkId = manager.addNetwork(configuration);
        if (networkId != -1) {
            return manager.enableNetwork(networkId, true);
        }

        return false;
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_WIFI_STATE)
    public String getConnectedWifi() {
        Context c = context.getApplicationContext();
        WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            throw new RuntimeException();
        }

        android.net.wifi.WifiInfo _wifiInfo = manager.getConnectionInfo();
        if (_wifiInfo != null) {
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.SSID = _wifiInfo.getSSID();
            wifiInfo.BSSID = _wifiInfo.getBSSID();
            wifiInfo.signalLevel = _wifiInfo.getRssi();
            return JsonUtils.toJson(wifiInfo);
        }

        return null;
    }

}
