package com.topevery.hybird.plugin.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topevery.hybird.utils.JsonUtils;

import java.util.List;

/**
 * @author wujie
 */
public class AppPlugin {
    private final Context context;

    public AppPlugin(Context context) {
        this.context = context;
    }

    public String getPackageInfo() {
        PkgInfo pkgInfo = new PkgInfo(context);
        return JsonUtils.toJson(pkgInfo);
    }

    public void installApp(String url) {
        if (!URLUtil.isFileUrl(url)) {
            return;
        }

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void uninstallApp(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }

        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public boolean appInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pInfos = manager.getInstalledPackages(0);
        if (pInfos != null) {
            for (PackageInfo pInfo : pInfos) {
                if (pInfo.packageName.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean launchApp(String packageName, String className, String data) {
        if (packageName == null || className == null) {
            return false;
        }

        ComponentName componentName = new ComponentName(packageName, className);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Bundle.class, new BundleDeserializer())
                .create();

        Bundle bundle = gson.fromJson(data, Bundle.class);

        try {
            Intent intent = new Intent();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
