package com.topevery.hybird.plugin.media.video;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;

/**
 * @author wujie
 */
public class VideoPlugin {
    private final Context context;

    public VideoPlugin(Context context) {
        this.context = context;
    }

    public void previewVideo(String url) {
        if (TextUtils.isEmpty(url))
            return;

        if (URLUtil.isFileUrl(url) || URLUtil.isNetworkUrl(url)) {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }

    public void pickVideo(String source, int _success, int _failure) {
        JsFunction failure = (JsFunction) Memory.getObject(_failure);

        DummyResultFragment fragment = null;
        if (source == null) {
            if (failure != null) {
                failure.invoke("source == null");
            }
        } else if (source.equals("gallery")) {
            fragment = new GalleryResultFragment(_success, _failure);
        } else if (source.equals("camera")) {
            fragment = new CameraResultFragment(_success, _failure);
        } else {
            if (failure != null) {
                failure.invoke("Invalid source.");
            }
        }
        if (fragment != null) {
            fragment.show(context, "pick_video");
        }
    }
}
