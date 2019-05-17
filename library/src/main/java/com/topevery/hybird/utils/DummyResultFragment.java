package com.topevery.hybird.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author wujie
 */
public class DummyResultFragment extends Fragment {
    private boolean mDismissed;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean waitForResult = false;
        Context context = getActivity();
        Intent intent = getTargetIntent(context);
        if (intent != null) {
            try {
                startActivityForResult(intent, 1);
                waitForResult = true;
            } catch (Exception e) {
                startActivityOnError(e);
            }
        }
        if (!waitForResult) {
            dismissInternal(true);
        }
    }

    public Intent getTargetIntent(Context context) {
        return null;
    }

    protected void startActivityOnError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 1) {
                onFragmentResult(requestCode, resultCode, data);
            }
        } finally {
            dismissInternal(true);
        }
    }

    public void onFragmentResult(int requestCode, int resultCode, Intent data) {

    }

    public void show(Context context, String tag) {
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
        show(fragmentManager, tag);
    }

    public void show(FragmentManager manager, String tag) {
        mDismissed = false;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    public void dismiss() {
        dismissInternal(false);
    }

    public void dismissAllowingStateLoss() {
        dismissInternal(true);
    }

    void dismissInternal(boolean allowStateLoss) {
        if (mDismissed) {
            return;
        }
        mDismissed = true;

        onDismiss();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        if (allowStateLoss) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }

    protected void onDismiss() {
        // dismiss
    }
}
