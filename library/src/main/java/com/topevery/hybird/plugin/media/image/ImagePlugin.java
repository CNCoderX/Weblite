package com.topevery.hybird.plugin.media.image;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.cncoderx.photopicker.ui.PreviewActivity;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.utils.DummyResultFragment;
import com.topevery.hybird.utils.JsonUtils;

/**
 * @author wujie
 */
public class ImagePlugin {
    private final Context context;

    public ImagePlugin(Context context) {
        this.context = context;
    }

    public void previewImage(String url) {
        if (TextUtils.isEmpty(url))
            return;

        if (URLUtil.isFileUrl(url) || URLUtil.isNetworkUrl(url)) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(Uri.parse(url), "image/*");
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void previewImages(int current, String _urls) {
        if (TextUtils.isEmpty(_urls))
            return;

        String[] urls = JsonUtils.fromJson(_urls, String[].class);

        try {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("current", current);
            intent.putExtra("urls", urls);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pickImage(String _options, int _success, int _failure) {
        JsFunction failure = (JsFunction) Memory.getObject(_failure);
        ImageOptions options = JsonUtils.fromJson(_options, ImageOptions.class);

        DummyResultFragment fragment = null;
        String source = options.source;
        if (source == null) {
            if (failure != null) {
                failure.invoke("source == null");
            }
        } else if (source.equals("gallery")) {
            fragment = new GalleryResultFragment(
                    options.maxWidth, options.maxHeight, _success, _failure);
        } else if (source.equals("camera")) {
            fragment = new CameraResultFragment(
                    options.maxWidth, options.maxHeight, _success, _failure);
        } else {
            if (failure != null) {
                failure.invoke("Invalid source.");
            }
        }
        if (fragment != null) {
            fragment.show(context, "pick_image");
        }
    }

    public void chooseImages(String _options, int _success, int _failure) {
        ImageOptions options = JsonUtils.fromJson(_options, ImageOptions.class);

        DummyResultFragment fragment = new GalleryMultiResultFragment(
                options.maxSize, options.maxWidth, options.maxHeight, _success, _failure);
        fragment.show(context, "choose_images");
    }
}
