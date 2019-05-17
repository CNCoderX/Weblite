package com.topevery.hybird.plugin.net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author wujie
 */
public abstract class StringCallback implements okhttp3.Callback {

    @Override
    public void onResponse(Call call, Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            onFailure(call, new IOException());
            return;
        }
        try {
            onResponse(call, response, body.string());
        } catch (IOException e) {
            onFailure(call, e);
        }
    }

    public abstract void onResponse(Call call, Response response, String string);
}
