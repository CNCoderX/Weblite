package com.topevery.hybird.plugin.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.topevery.hybird.reflect.JsObject;

/**
 * @author wujie
 */
public class Preferences extends JsObject {
    private final SharedPreferences mPreferences;

    public Preferences(Context context, String name) {
        mPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    public String get(String key) {
        return mPreferences.getString(key, null);
    }

    public boolean set(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public boolean remove(String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public boolean clear() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        return editor.commit();
    }
}
