package com.topevery.hybird.plugin.media.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.JsObject;

/**
 * @author wujie
 */
public class SoundPlayer extends JsObject implements IPlayer {
    private SoundPool mSoundPool;

    private String mSource;
    private Options mOptions;
    private int mSoundId;
    private int mState = STATE_IDLE;
    private JsFunction mErrorCallback;

    public static class Options {
        public int quality = 0;
        public float volume = 1.0f;
    }

    public SoundPlayer(Context context) {
        this(context, new Options());
    }

    public SoundPlayer(Context context, Options options) {
        mOptions = options;

        int quality = options == null ? 0 : options.quality;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, quality);
    }

    public void setErrorCallback(int error) {
        mErrorCallback = (JsFunction) Memory.getObject(error);
    }

    public int getState() {
        return mState;
    }

    @Override
    public void prepare(String path) {
        if (!URLUtil.isFileUrl(path)) {
            if (mErrorCallback != null) {
                mErrorCallback.invoke("invalid path");
            }
            return;
        }

        if (mState == STATE_IDLE) {
            mState = STATE_INITED;
        }

        if (mState == STATE_INITED
                || mState == STATE_STOPPED) {
            mSource = Uri.parse(path).getPath();
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    mState = STATE_PREPARED;
                }
            });
            mSoundId = mSoundPool.load(mSource, 1);
            if (mSoundId == 0) {
                if (mErrorCallback != null) {
                    mErrorCallback.invoke("error loading source");
                }
            }
        }
    }

    @Override
    public void start() {
        if (mState == STATE_PREPARED
                || mState == STATE_COMPLETED) {
            float volume = mOptions == null ? 1.0f : mOptions.volume;
            if (mSoundId != 0) {
                mSoundId = mSoundPool.play(mSoundId, volume, volume, 1, 0, 1);
                if (mSoundId != 0) {
                    mState = STATE_STARTED;
                }
            }
        }
    }

    @Override
    public void resume() {
        if (mState == STATE_PAUSED) {
            if (mSoundId != 0) {
                mSoundPool.resume(mSoundId);
                mState = STATE_STARTED;
            }
        }
    }

    @Override
    public void pause() {
        if (mState == STATE_STARTED) {
            if (mSoundId != 0) {
                mSoundPool.pause(mSoundId);
                mState = STATE_PAUSED;
            }
        }
    }

    @Override
    public void stop() {
        if (mState == STATE_STARTED
                || mState == STATE_PAUSED
                || mState == STATE_COMPLETED) {
            if (mSoundId != 0) {
                mSoundPool.stop(mSoundId);
                mState = STATE_STOPPED;
            }
        }
    }

    @Override
    public void reset() {
        if (mSoundId != 0) {
            mSoundPool.unload(mSoundId);
        }
        mSoundId = 0;
        mState = STATE_IDLE;
    }

    @Override
    public void release() {
        if (mErrorCallback != null) {
            Memory.releaseObject(mErrorCallback);
        }

        if (mSoundId != 0) {
            mSoundPool.unload(mSoundId);
        }
        mSoundId = 0;
        mSoundPool.setOnLoadCompleteListener(null);
        mSoundPool.release();
    }
}
