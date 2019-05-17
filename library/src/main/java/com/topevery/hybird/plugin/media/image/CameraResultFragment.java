package com.topevery.hybird.plugin.media.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.ImageResizer;
import com.topevery.hybird.utils.Storage;
import com.topevery.hybird.reflect.JsFunction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class CameraResultFragment extends DummyResultFragment {
    private int mMaxWidth;
    private int mMaxHeight;

    private File mImageFile;

    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;


    public CameraResultFragment(int maxWidth, int maxHeight, int success, int failure) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = dateFormat.format(new Date());

        File cameraDir = Storage.getCameraDir(context);
        mImageFile = new File(cameraDir, "IMG_" + dateTime + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));
        return intent;
    }

    @Override
    protected void startActivityOnError(Exception e) {
        notifyFailure("Failed to open the camera.");
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            notifyFailure("operation canceled.");
            return;
        }

        Context context = getActivity();
        final String imagePath = mImageFile.getAbsolutePath();
        MediaScannerConnection.scanFile(
                context,
                new String[] {imagePath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        if (imagePath.equals(path)) {
                            CameraResultFragment.super.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        String imagePath = mImageFile.getAbsolutePath();
        Uri sourceImage = Uri.fromFile(mImageFile);
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
