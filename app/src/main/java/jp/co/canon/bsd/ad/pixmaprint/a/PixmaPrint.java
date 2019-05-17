package jp.co.canon.bsd.ad.pixmaprint.a;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * @author wujie
 */
public class PixmaPrint {
    public static final String PLUGIN_PACKAGE_NAME = "jp.co.canon.bsd.ad.pixmaprint";
    public static final String PLUGIN_LOCAL_CONVERTER = "jp.co.canon.bsd.ad.pixmaprint.ui.activity.LocalFileConverterActivity";
    public static final String PLUGIN_REMOTE_CONVERTER = "jp.co.canon.bsd.ad.pixmaprint.ui.activity.RemoteFileConverterActivity";

    public static boolean printLocalDocument(Context context, String path) {
        try {
            Intent intent = new Intent();
            intent.putExtra("params.PRINT", new e(path));
            intent.setComponent(new ComponentName(PLUGIN_PACKAGE_NAME, PLUGIN_LOCAL_CONVERTER));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public static boolean printRemoteDocument(Context context, String path) {
        try {
            Intent intent = new Intent();
            intent.putExtra("params.PRINT", new e(path));
            intent.setComponent(new ComponentName(PLUGIN_PACKAGE_NAME, PLUGIN_REMOTE_CONVERTER));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
