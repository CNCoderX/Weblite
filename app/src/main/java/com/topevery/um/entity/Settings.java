package com.topevery.um.entity;

import android.content.Context;
import android.text.TextUtils;

import com.topevery.um.BuildConfig;

/**
 * @author wujie
 */
public class Settings {
    private static final String DEFAULT_SERVER_IP = "192.168.20.215";
    private static final int DEFAULT_SERVER_PORT = 0;

    public static final String PREF_NAME = "settings.pref";

    public static void setServerIp(Context context, String ip) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("server_ip", ip)
                .apply();
    }

    public static void setServerPort(Context context, int port) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt("server_port", port)
                .apply();
    }

    public static void setServerHost(Context context, String ip, int port) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("server_ip", ip)
                .putInt("server_port", port)
                .apply();
    }

    public static String getServerIp(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("server_ip", DEFAULT_SERVER_IP);
    }

    public static int getServerPort(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt("server_port", DEFAULT_SERVER_PORT);
    }

    public static String getServerHost(Context context) {
        String ip = getServerIp(context);
        int port = getServerPort(context);
        if (TextUtils.isEmpty(ip)) {
            return "";
        }
        if (port <= 0) {
            return ip;
        }
        return ip + ":" + port;
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

}
