package com.topevery.hybird.plugin.media.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.Storage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class CameraResultFragment extends DummyResultFragment {
    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    private File mVideoFile;

    public CameraResultFragment(int success, int failure) {
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = dateFormat.format(new Date());

        File cameraDir = Storage.getCameraDir(getActivity());
        mVideoFile = new File(cameraDir, "VID_" + dateTime + ".mp4");

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mVideoFile));
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
        final String videoPath = mVideoFile.getAbsolutePath();
        MediaScannerConnection.scanFile(
                context,
                new String[] {videoPath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        if (videoPath.equals(path)) {
                            CameraResultFragment.super.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        Uri videoUri = Uri.fromFile(mVideoFile);
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
