package com.topevery.hybird.plugin.file;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.FileUtils;

import java.io.File;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class ChooseFileResultFragment extends DummyResultFragment {
    private String mAcceptType;

    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    public ChooseFileResultFragment(String acceptType, int success, int failure) {
        mAcceptType = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mAcceptType);
        return intent;
    }

    @Override
    protected void startActivityOnError(Exception e) {
        notifyFailure("Failed to open the file chooser");
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            notifyFailure("operation canceled.");
            return;
        }

        String filePath = FileUtils.getPathFromUri(getActivity(), data.getData());
        Uri uri = Uri.fromFile(new File(filePath));
        notifySuccess(uri);
    }

    void notifySuccess(Uri uri) {
        if (mSuccessCallback != null) {
            mSuccessCallback.invoke(uri.toString());
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
