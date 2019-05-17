package com.topevery.hybird.plugin.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author wujie
 */
@SuppressLint("ValidFragment")
public class ScanCodeResultFragment extends DummyResultFragment {
    private String mScanType;

    private JsFunction mSuccessCallback;
    private JsFunction mFailureCallback;

    public ScanCodeResultFragment(String scanType, int success, int failure) {
        mScanType = scanType;
        mSuccessCallback = (JsFunction) Memory.getObject(success);
        mFailureCallback = (JsFunction) Memory.getObject(failure);
    }

    @Override
    public Intent getTargetIntent(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);

        Collection<String> scanTypeFormat = getScanTypeFormat(mScanType);
        if (scanTypeFormat != null && !scanTypeFormat.isEmpty()) {
            String _scanTypeFormat = TextUtils.join(",", scanTypeFormat);
            intent.putExtra(Intents.Scan.FORMATS, _scanTypeFormat);
        } else {
            notifyFailure("Invalid scan type.");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }

    @Override
    protected void startActivityOnError(Exception e) {
        notifyFailure("Failed to open the camera.");
    }

    private static Collection<String> getScanTypeFormat(String scanType) {
        Collection<String> format = new ArrayList<>();
        if (scanType != null) {
            if (scanType.equals("BAR_CODE")) {
                format.addAll(IntentIntegrator.ONE_D_CODE_TYPES);
            } else if (scanType.equals("QR_CODE")) {
                format.add(IntentIntegrator.QR_CODE);
            } else if (scanType.equals("DATA_MATRIX")) {
                format.add(IntentIntegrator.DATA_MATRIX);
            } else if (scanType.equals("PDF_417")) {
                format.add(IntentIntegrator.PDF_417);
            }
        }
        return format;
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            notifyFailure("operation canceled.");
            return;
        }

        String result = data.getStringExtra(Intents.Scan.RESULT);
        notifySuccess(result);
    }

    void notifySuccess(String result) {
        if (mSuccessCallback != null) {
            mSuccessCallback.invoke(result);
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
