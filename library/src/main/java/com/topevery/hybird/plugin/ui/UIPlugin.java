package com.topevery.hybird.plugin.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.topevery.hybird.utils.JsonUtils;

/**
 * @author wujie
 */
public class UIPlugin {
    private final Context context;

    public UIPlugin(Context context) {
        this.context = context;
    }

    public void showToast(String text) {
        Context context = this.context.getApplicationContext();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void showNotification(String _options) {
        NotificationOptions options = JsonUtils.fromJson(_options, NotificationOptions.class);
        NotificationManager notifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(options.contentTitle);
        builder.setContentText(options.contentText);
        builder.setTicker(options.ticker);
        builder.setWhen(System.currentTimeMillis());
        builder.setPriority(options.priority);
        builder.setNumber(options.number);
        builder.setAutoCancel(options.autoCancel);
        builder.setOngoing(options.ongoing);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            builder.setSmallIcon(options.getAppIconRes(context));
            builder.setLargeIcon(BitmapFactory.decodeResource(
                    context.getResources(), options.getAppIconRes(context)));
        } else {
            builder.setSmallIcon(options.getSmallIcon(context));
            builder.setLargeIcon(options.getLargeIcon(context));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(options.category);
        }
        Uri soundUri = options.getSoundUri(context);
        if (soundUri == null) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        } else {
            builder.setSound(soundUri);
        }
        if (options.vibrate != null) {
            builder.setVibrate(options.vibrate);
        }
        builder.setContentInfo(options.contentInfo);
        builder.setOnlyAlertOnce(options.onlyAlertOnce);
        builder.setContentIntent(options.getPendingIntent(context));
        if (notifyManager != null) {
            notifyManager.notify(options.id, builder.getNotification());
        }
    }

    public void removeNotification(int id) {
        NotificationManager notifyManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (notifyManager != null) {
            notifyManager.cancel(id);
        }
    }
}
