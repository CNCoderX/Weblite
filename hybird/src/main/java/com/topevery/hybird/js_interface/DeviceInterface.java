package com.topevery.hybird.js_interface;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;

import com.topevery.hybird.plugin.device.DevicePlugin;

import org.xwalk.core.JavascriptInterface;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * @author wujie
 */
public class DeviceInterface {
    private DevicePlugin mDevicePlugin;

    public DeviceInterface(Context context) {
        mDevicePlugin = new DevicePlugin(context);
    }

    @JavascriptInterface
    public String getDeviceInfo() {
        return mDevicePlugin.getDeviceInfo();
    }

    @JavascriptInterface
    public String getIMEI() {
        return mDevicePlugin.getIMEI();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @JavascriptInterface
    public int getNetworkType() {
        return mDevicePlugin.getNetworkType();
    }

    @RequiresPermission(anyOf = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION})
    @JavascriptInterface
    public void getLocation(int success, int failure) {
        mDevicePlugin.getLocation(success, failure);
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    @JavascriptInterface
    public void makePhoneCall(String phoneNumber) {
        mDevicePlugin.makePhoneCall(phoneNumber);
    }

    @JavascriptInterface
    public void makeSMS(String phoneNumber, String message) {
        mDevicePlugin.makeSMS(phoneNumber, message);
    }

    @JavascriptInterface
    public void setClipboardData(String data) {
        mDevicePlugin.setClipboardData(data);
    }

    @JavascriptInterface
    public String getClipboardData() {
        return mDevicePlugin.getClipboardData();
    }

    @JavascriptInterface
    public void captureScreen(int success, int failure) {
        mDevicePlugin.captureScreen(success, failure);
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    @JavascriptInterface
    public void vibrate(int duration) {
        mDevicePlugin.vibrate(duration);
    }

    @JavascriptInterface
    public void printDocument(String name, String path) {
        mDevicePlugin.printDocument(name, path);
    }

    @JavascriptInterface
    public void printPicture(String name, String path) {
        mDevicePlugin.printPicture(name, path);
    }

    @JavascriptInterface
    public void scanCode(String type, int success, int failure) {
        mDevicePlugin.scanCode(type, success, failure);
    }

    @RequiresPermission(android.Manifest.permission.CHANGE_WIFI_STATE)
    @JavascriptInterface
    public boolean openWifi() {
        return mDevicePlugin.openWifi();
    }

    @RequiresPermission(android.Manifest.permission.CHANGE_WIFI_STATE)
    @JavascriptInterface
    public boolean closeWifi() {
        return mDevicePlugin.closeWifi();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_WIFI_STATE)
    @JavascriptInterface
    public String getWifiList() {
        return mDevicePlugin.getWifiList();
    }

    @RequiresPermission(allOf = {
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE})
    @JavascriptInterface
    public boolean connectWifi(String SSID, String password, String cipherType) {
        return mDevicePlugin.connectWifi(SSID, password, cipherType);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_WIFI_STATE)
    @JavascriptInterface
    public String getConnectedWifi() {
        return mDevicePlugin.getConnectedWifi();
    }
}
