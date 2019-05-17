package com.topevery.um.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.topevery.um.utils.AESUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author wujie
 */
public class Authorizations {
    private static final String PREF_NAME = "auth.pref";
    private static final String KEY_USER = "username";
    private static final String KEY_PWD = "password";
    private static final String KEY_PROF = "profile";
    private static final String AES_SEED = "topevery.auth";

    private SharedPreferences mPreferences;

    private Authorizations(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private Authorizations(Context context,
                           String username,
                           String password,
                           Profile profile) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        if (!TextUtils.isEmpty(username)) {
            setUsername(editor, username);
        }
        if (!TextUtils.isEmpty(password)) {
            setPassword(editor, password);
        }
        if (profile != null) {
            setProfile(editor, profile);
        }
        editor.apply();
    }

    private void setUsername(SharedPreferences.Editor editor, String username) {
        editor.putString(KEY_USER, username);
    }

    private void setPassword(SharedPreferences.Editor editor, String password) {
        editor.putString(KEY_PWD, password);
//        if (!TextUtils.isEmpty(password)) {
//            try {
//                String encrypted = AESUtils.encrypt(AES_SEED, password);
//                editor.putString(KEY_PWD, encrypted);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void setProfile(SharedPreferences.Editor editor, @NonNull Profile profile) {
        String json = new Gson().toJson(profile);
        String encoded;
        try {
            encoded = Base64.encodeToString(
                    json.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            encoded = Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
        }
        editor.putString(KEY_PROF, encoded);
    }

    public String getUsername() {
        return mPreferences.getString(KEY_USER, "");
    }

    public String getPassword() {
        return mPreferences.getString(KEY_PWD, "");
//        String password = mPreferences.getString(KEY_PWD, "");
//        if (!TextUtils.isEmpty(password)) {
//            try {
//                return AESUtils.decrypt(AES_SEED, password);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return "";
    }

    @Nullable
    public Profile getProfile() {
        String prof = mPreferences.getString(KEY_PROF, "");
        if (!TextUtils.isEmpty(prof)) {
            byte[] decoded = Base64.decode(prof, Base64.DEFAULT);
            String json;
            try {
                json = new String(decoded, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                json = new String(decoded);
            }
            Profile profile;
            try {
                profile = new Gson().fromJson(json, Profile.class);
            } catch (JsonSyntaxException e) {
                profile = null;
            }
            return profile;
        }
        return null;
    }

    public void deleteProfile() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_PROF).apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear().apply();
    }

    public static Authorizations getInstance(Context context) {
        return new Authorizations(context);
    }

    public static class Builder {
        private Context context;
        private String username;
        private String password;
        private Profile profile;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setProfile(Profile profile) {
            this.profile = profile;
            return this;
        }

        public Authorizations build() {
            return new Authorizations(context, username, password, profile);
        }
    }

}
