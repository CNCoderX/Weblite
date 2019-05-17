package com.topevery.hybird.plugin.media.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.cncoderx.photopicker.ui.GalleryActivity;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.ImageResizer;
import com.topevery.hybird.utils.JsonUtils;
import com.topevery.hybird.utils.Storage;

import java.io.File;
import java.io.IOException;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class GalleryMultiResultFragment extends DummyResultFragment {
    private int mMaxSize;
    private int mMaxWidth;
    private int mMaxHeight;

    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    public GalleryMultiResultFragment(int maxSize, int maxWidth, int maxHeight, int success, int failure) {
        mMaxSize = maxSize;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        intent.putExtra("maxSize", mMaxSize);
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

        String[] imagePaths = data.getStringArrayExtra("data");
        int size = imagePaths.length;

        String[] scaledPaths, sourcePaths;
        File tempDir = Storage.getTempDir(getActivity());
        ImageResizer imageResizer = new ImageResizer(tempDir);

        scaledPaths = new String[size];
        sourcePaths = new String[size];
        for (int i = 0; i < size; i++) {
            sourcePaths[i] = parseImagePath(imagePaths[i]);
            try {
                String scaledPath = imageResizer.resizeImageIfNeeded(imagePaths[i], mMaxWidth, mMaxHeight);
                scaledPaths[i] = parseImagePath(scaledPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        notifySuccess(scaledPaths, sourcePaths);
    }

    private String parseImagePath(String imagePath) {
        File f = new File(imagePath);
        Uri uri = Uri.fromFile(f);
        return uri.toString();
    }

    void notifySuccess(String[] scaledImage, String[] sourceImage) {
        if (mSuccessCallback != null) {
            mSuccessCallback.invoke(
                    JsonUtils.toJson(scaledImage),
                    JsonUtils.toJson(sourceImage));
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
