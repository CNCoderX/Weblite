package com.topevery.hybird.reflect;

import android.util.SparseArray;

/**
 * @author wujie
 */
public class Memory {
    private static final SparseArray<JsObject> objects = new SparseArray<>();

    public static final int NULL = -1;

    public static int createObject(JsObjectFactory factory) {
        if (factory == null) {
            return NULL;
        }
        JsObject object = factory.create();
        if (object == null) {
            return NULL;
        }
        synchronized (Memory.class) {
            int pointer = object.pointer;
            objects.put(pointer, object);
            return pointer;
        }
    }

    public static JsObject getObject(int pointer) {
        synchronized (Memory.class) {
            return objects.get(pointer);
        }
    }

    public static void releaseObject(int pointer) {
        synchronized (Memory.class) {
            JsObject object = objects.get(pointer);
            if (object != null) {
                object.release();
            }
            objects.remove(pointer);
        }
    }

    public static void releaseObject(JsObject object) {
        synchronized (Memory.class) {
            if (object != null) {
                object.release();
                objects.remove(object.pointer);
            }
        }
    }

    public interface JsObjectFactory {
        JsObject create();
    }
}
