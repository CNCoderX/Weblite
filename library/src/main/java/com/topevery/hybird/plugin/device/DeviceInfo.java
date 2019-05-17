package com.topevery.hybird.plugin.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * @author wujie
 */
public class DeviceInfo {
    public Version version;
    public String board;
    public String bootloader;
    public String brand;
    public String device;
    public String display;
    public String fingerprint;
    public String hardware;
    public String host;
    public String id;
    public String manufacturer;
    public String model;
    public String product;
    public String tags;
    public String type;
    public boolean isEmulator;

    public DeviceInfo() {
        board = Build.BOARD;
        bootloader = Build.BOOTLOADER;
        brand = Build.BRAND;
        device = Build.DEVICE;
        display = Build.DISPLAY;
        fingerprint = Build.FINGERPRINT;
        hardware = Build.HARDWARE;
        host = Build.HOST;
        id = Build.ID;
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        product = Build.PRODUCT;
        tags = Build.TAGS;
        type = Build.TYPE;
        isEmulator = isEmulator();
        version = new Version();
    }

    public boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    @SuppressLint("MissingPermission")
    public String getIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return manager.getDeviceId();
        } else {
            return manager.getImei();
        }
    }

    public static class Version {
        public String baseOS;
        public int previewSdkInt;
        public String securityPatch;
        public String codename;
        public String incremental;
        public String release;
        public int sdkInt;

        public Version() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                baseOS = Build.VERSION.BASE_OS;
                previewSdkInt = Build.VERSION.PREVIEW_SDK_INT;
                securityPatch = Build.VERSION.SECURITY_PATCH;
            }
            codename = Build.VERSION.CODENAME;
            incremental = Build.VERSION.INCREMENTAL;
            release = Build.VERSION.RELEASE;
            sdkInt = Build.VERSION.SDK_INT;
        }
    }
}
