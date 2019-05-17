package com.topevery.um;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.Memory;
import com.umeng.message.UmengMessageService;

import org.android.agoo.common.AgooConstants;

/**
 * @author wujie
 */
public class UMPushMessageService extends UmengMessageService {
    private static int _handler = Memory.NULL;

    public static void setHandler(int handler) {
        _handler = handler;
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        String id = intent.getStringExtra(AgooConstants.MESSAGE_ID);
        String time = intent.getStringExtra(AgooConstants.MESSAGE_TIME);
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);

        pushMessageToDb(id, message, time);
        Log.i("UMPush", "receive:" + message);

        JsFunction handler = (JsFunction) Memory.getObject(_handler);
        if (handler != null) {
            handler.invoke(message);
        }
    }

    private void pushMessageToDb(String id, String message, String time) {
        SQLiteDatabase db = UPushDBHelper.getInstance(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", id);
        values.put("MESSAGE", message);
        values.put("TIME", time);
        db.insert("UMENG_MSG", null, values);
    }
}
