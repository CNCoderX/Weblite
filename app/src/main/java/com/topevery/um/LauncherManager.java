package com.topevery.um;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author wujie
 */
public class LauncherManager {
    private Context mContext;
    private ComponentName mComponentName;

    public LauncherManager(Context context, ComponentName componentName) {
        mContext = context;
        mComponentName = componentName;
    }

    public void switchLauncher(ComponentName componentName) {
        if (!mComponentName.equals(componentName)) {
            disableComponent(mComponentName);
            enableComponent(componentName);
            mComponentName = componentName;
        }
    }

    private void enableComponent(ComponentName compName) {
        mContext.getPackageManager().setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void disableComponent(ComponentName compName) {
        mContext.getPackageManager().setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
