package com.topevery.hybird.plugin.media.audio;

import android.content.Context;
import android.media.AudioFormat;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.JsObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @author wujie
 */
public abstract class Recorder extends JsObject implements IPlayer {
    public static final String FORMAT_MP3 = "mp3";
    public static final String FORMAT_WAV = "wav";

    protected Options mOptions;
    private JsFunction mErrorCallback;
    private WeakReference<Context> mContext;

    public Recorder(Context context, Options options) {
        mContext = new WeakReference<>(context);
        mOptions = options;
    }

    public Context getContext() {
        return mContext.get();
    }

    public void setErrorCallback(int error) {
        mErrorCallback = (JsFunction) Memory.getObject(error);
    }

    protected void errorMessage(String errMsg) {
        if (mErrorCallback != null) {
            mErrorCallback.invoke(errMsg);
        }
    }

    public abstract int getState();

    public abstract File getFile();

    @Override
    public void release() {
        if (mErrorCallback != null) {
            Memory.releaseObject(mErrorCallback);
        }
    }

    public static class Options {
        public int sampleRate = 44100;
        public int channels = 1;
        public int bitPerSample = 16;

        public int getChannelConfig() {
            return channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
        }

        public int getAudioFormat() {
            return bitPerSample == 8 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;
        }

        public int getEncodeBitRate() {
            if (sampleRate < 12000) {
                return 24;
            }
            if (sampleRate < 22050) {
                return 32;
            }
            if (sampleRate < 32000) {
                return 48;
            }
            if (sampleRate < 44100) {
                return 64;
            }
            return 96;
        }
    }
}
