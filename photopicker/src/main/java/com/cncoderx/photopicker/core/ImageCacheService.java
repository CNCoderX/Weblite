package com.cncoderx.photopicker.core;

import android.content.Context;

import com.cncoderx.photopicker.io.BlobCache;
import com.cncoderx.photopicker.io.BytesBufferPool;
import com.cncoderx.photopicker.io.CacheManager;
import com.cncoderx.photopicker.utils.MathUtils;
import com.cncoderx.photopicker.utils.Thumbnail;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author wujie
 */
public class ImageCacheService {
    private static final String IMAGE_CACHE_FILE = "imgcache";
    private static final int IMAGE_CACHE_MAX_ENTRIES = 5000;
    private static final int IMAGE_CACHE_MAX_BYTES = 200 * 1024 * 1024;
    private static final int IMAGE_CACHE_VERSION = 7;

    private BlobCache mCache;

    public ImageCacheService(Context context) {
        mCache = CacheManager.getCache(context, IMAGE_CACHE_FILE,
                IMAGE_CACHE_MAX_ENTRIES, IMAGE_CACHE_MAX_BYTES,
                IMAGE_CACHE_VERSION);
    }

    public boolean getImageData(String path, long timeModified, Thumbnail type, BytesBufferPool.BytesBuffer buffer) {
        byte[] key = makeKey(path, timeModified, type);
        long cacheKey = MathUtils.crc64Long(key);
        try {
            BlobCache.LookupRequest request = new BlobCache.LookupRequest();
            request.key = cacheKey;
            request.buffer = buffer.data;
            synchronized (mCache) {
                if (!mCache.lookup(request)) return false;
            }
            if (isSameKey(key, request.buffer)) {
                buffer.data = request.buffer;
                buffer.offset = key.length;
                buffer.length = request.length - buffer.offset;
                return true;
            }
        } catch (IOException ex) {
            // ignore.
        }
        return false;
    }

    public void putImageData(String path, long timeModified, Thumbnail type, byte[] value) {
        byte[] key = makeKey(path, timeModified, type);
        long cacheKey = MathUtils.crc64Long(key);
        ByteBuffer buffer = ByteBuffer.allocate(key.length + value.length);
        buffer.put(key);
        buffer.put(value);
        synchronized (mCache) {
            try {
                mCache.insert(cacheKey, buffer.array());
            } catch (IOException ex) {
                // ignore.
            }
        }
    }

    public void clearImageData(String path, long timeModified, Thumbnail type) {
        byte[] key = makeKey(path, timeModified, type);
        long cacheKey = MathUtils.crc64Long(key);
        synchronized (mCache) {
            try {
                mCache.clearEntry(cacheKey);
            } catch (IOException ex) {
                // ignore.
            }
        }
    }

    private static byte[] makeKey(String path, long timeModified, Thumbnail type) {
        return MathUtils.getBytes(path + "+" + timeModified + "+" + type);
    }

    private static boolean isSameKey(byte[] key, byte[] buffer) {
        int n = key.length;
        if (buffer.length < n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (key[i] != buffer[i]) {
                return false;
            }
        }
        return true;
    }
}
