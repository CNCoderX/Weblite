package com.topevery.hybird.plugin.media.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.FileUtils;

import java.io.File;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class GalleryResultFragment extends DummyResultFragment {
    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    public GalleryResultFragment(int success, int failure) {
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        return intent;
    }

    @Override
    protected void startActivityOnError(Exception e) {
        notifyFailure("Failed to open the gallery.");
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            notifyFailure("operation canceled.");
            return;
        }

        String videoPath = FileUtils.getPathFromUri(getActivity(), data.getData());
        Uri videoUri = Uri.fromFile(new File(videoPath));
        notifySuccess(videoUri);
    }

    void notifySuccess(Uri videoUri) {
        if (mSuccessCallback != null) {
            mSuccessCallback.invoke(videoUri.toString());
        }
    }

    void notifyFailure(String errMsg) {
        if (mFailureCallback != null) {
            mFailureCallback.invoke(errMsg);
        }
    }

    @Override
    protected void onDismiss() {
        if (mSuccessCallback != null) {
            Memory.releaseObject(mSuccessCallback);
        }
        if (mFailureCallback != null) {
            Memory.releaseObject(mFailureCallback);
        }
    }

}
