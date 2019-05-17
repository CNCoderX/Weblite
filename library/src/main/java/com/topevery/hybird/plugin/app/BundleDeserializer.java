package com.topevery.hybird.plugin.app;

import android.os.Bundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author wujie
 */
public class BundleDeserializer implements JsonDeserializer<Bundle> {

    @Override
    public Bundle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!jsonElement.isJsonArray()) {
            return null;
        }

        Bundle bundle = new Bundle();

        JsonArray rootElement = jsonElement.getAsJsonArray();
        for (JsonElement element : rootElement) {
            if (element.isJsonObject()) {
                IntentData intentData = getElementAsIntentData(element);
                Converter converter = ConverterFactory.create(intentData.type);
                putExtraToBundle(bundle, converter, intentData.key, intentData.value);
            }
        }

        return bundle;
    }

    public IntentData getElementAsIntentData(JsonElement element) {
        IntentData data = null;
        if (element.isJsonObject()) {
            data = new IntentData();
            JsonObject object = element.getAsJsonObject();
            data.key = object.get("key").getAsString();
            data.value = object.get("value").getAsString();
            data.type = object.get("type").getAsString();
        }
        return data;
    }

    public static void putExtraToBundle(Bundle bundle, Converter converter, String key, String extra) {
        Object value = null;
        if (converter != null) {
            value = converter.convert(extra);
        }
        if (bundle != null && value != null) {
            if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof boolean[]) {
                bundle.putBooleanArray(key, (boolean[]) value);
            } else if (value instanceof Byte) {
                bundle.putByte(key, (Byte) value);
            } else if (value instanceof byte[]) {
                bundle.putByteArray(key, (byte[]) value);
            } else if (value instanceof Short) {
                bundle.putShort(key, (Short) value);
            } else if (value instanceof short[]) {
                bundle.putShortArray(key, (short[]) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof int[]) {
                bundle.putIntArray(key, (int[]) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            } else if (value instanceof long[]) {
                bundle.putLongArray(key, (long[]) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (Float) value);
            } else if (value instanceof float[]) {
                bundle.putFloatArray(key, (float[]) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof double[]) {
                bundle.putDoubleArray(key, (double[]) value);
            } else if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof String[]) {
                bundle.putStringArray(key, (String[]) value);
            }
        }
    }
}
