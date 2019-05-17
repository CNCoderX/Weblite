package com.topevery.um.utils;

import android.view.View;

/**
 * @author wujie
 */
public abstract class CountClickListener implements View.OnClickListener {
    private final int mTotalCount;
    private int mStateCount;
    private long mInterval = 800;
    private long lastSecondMillis;

    public CountClickListener(int count) {
        mTotalCount = count;
        lastSecondMillis = System.currentTimeMillis();
    }

    public CountClickListener(int count, long interval) {
        mTotalCount = count;
        mInterval = interval;
        lastSecondMillis = System.currentTimeMillis();
    }

    @Override
    public final void onClick(View v) {
        if (mInterval < System.currentTimeMillis() - lastSecondMillis) {
            mStateCount = 1;
        } else {
            mStateCount++;
        }
        onSingleClick(v, mStateCount);
        if (mStateCount >= mTotalCount) {
            onCountClick(v);
            mStateCount = 0;
        }
        lastSecondMillis = System.currentTimeMillis();
    }

    public void onSingleClick(View v, int count) {
    }

    public abstract void onCountClick(View v);

}
