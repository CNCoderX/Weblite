package com.topevery.hybird.plugin.ui;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * @author wujie
 */
public class NotificationOptions {
    public int id;
    public String contentTitle;
    public String contentText;
    public String ticker;
    public int priority = Notification.PRIORITY_DEFAULT;
    public int number;
    public boolean autoCancel;
    public boolean ongoing;
    public String smallIcon;
    public String largeIcon;
    public String category;
    public String contentInfo;
    public String sound;
    public long[] vibrate;
    public boolean onlyAlertOnce;
    public String navigateTo;

    @TargetApi(Build.VERSION_CODES.M)
    public Icon getSmallIcon(Context context) {
        if (smallIcon != null) {
            File file = new File(smallIcon);
            if (file.exists()) {
                return Icon.createWithFilePath(smallIcon);
            }
        }
        return Icon.createWithResource(context.getPackageName(), getAppIconRes(context));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Icon getLargeIcon(Context context) {
        if (largeIcon != null) {
            File file = new File(largeIcon);
            if (file.exists()) {
                return Icon.createWithFilePath(largeIcon);
            }
        }
        return Icon.createWithResource(context.getPackageName(), getAppIconRes(context));
    }

    public int getAppIconRes(Context context) {
        int iconRes = 0;
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            iconRes = applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return iconRes;
    }

    public Uri getSoundUri(Context context) {
        if (sound == null) {
            return null;
        }
        File file = new File(sound);
        if (!file.exists()) {
            return null;
        }
        return Uri.fromFile(file);
    }

    public PendingIntent getPendingIntent(Context context) {
        if (navigateTo == null) {
            return null;
        }

        Intent intent = new Intent("com.topevery.um.action.WEB");
        intent.setData(Uri.parse(navigateTo));
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
