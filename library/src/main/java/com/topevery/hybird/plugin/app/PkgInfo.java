package com.topevery.hybird.plugin.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author wujie
 */
public class PkgInfo {
    public String appName;
    public String pkgName;
    public int versionCode;
    public String versionName;

    public PkgInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            pkgName = context.getPackageName();
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
