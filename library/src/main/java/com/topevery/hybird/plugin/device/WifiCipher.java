package com.topevery.hybird.plugin.device;

import android.text.TextUtils;

/**
 * @author wujie
 */
public class WifiCipher {
    public static String WPA = "wpa";
    public static String WEP = "wep";
    public static String NONE = "none";

    public static String getCipherType(String capabilities) {
        if (TextUtils.isEmpty(capabilities)) {
            return WPA;
        }
        if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
            return WPA;
        }
        if (capabilities.contains("WEP") || capabilities.contains("wep")) {
            return WEP;
        }
        return NONE;
    }
}
