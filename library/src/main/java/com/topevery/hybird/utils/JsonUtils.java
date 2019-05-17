package com.topevery.hybird.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author wujie
 */
public class JsonUtils {

    public static <T> T fromJson(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static String toJson(Object src) {
        return new Gson().toJson(src);
    }
}
