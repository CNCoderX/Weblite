package com.topevery.hybird.plugin.media.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.FileUtils;
import com.topevery.hybird.utils.ImageResizer;
import com.topevery.hybird.utils.Storage;

import java.io.File;
import java.io.IOException;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class GalleryResultFragment extends DummyResultFragment {
    private int mMaxWidth;
    private int mMaxHeight;

    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    public GalleryResultFragment(int maxWidth, int maxHeight, int success, int failure) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
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

        String imagePath = FileUtils.getPathFromUri(getActivity(), data.getData());
        Uri sourceImage = Uri.fromFile(new File(imagePath));
        try {
            File tempDir = Storage.getTempDir(getActivity());
            ImageResizer imageResizer = new ImageResizer(tempDir);
            String scaledImagePath = imageResizer.resizeImageIfNeeded(imagePath, mMaxWidth, mMaxHeight);
            Uri scaledImage = Uri.fromFile(new File(scaledImagePath));
            notifySuccess(scaledImage, sourceImage);
        } catch (IOException e) {
            notifySuccess(sourceImage, sourceImage);
        }
    }

    void notifySuccess(Uri scaledImage, Uri sourceImage) {
        if (mSuccessCallback != null) {
            mSuccessCallback.invoke(scaledImage.toString(), sourceImage.toString());
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
