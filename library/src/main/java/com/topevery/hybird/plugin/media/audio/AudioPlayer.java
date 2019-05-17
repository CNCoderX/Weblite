package com.topevery.hybird.plugin.media.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.JsFunction;
import com.topevery.hybird.reflect.JsObject;
import com.topevery.hybird.reflect.Memory;

import java.io.IOException;

/**
 * @author wujie
 */
public class AudioPlayer extends JsObject implements
        IPlayer,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private MediaPlayer mPlayer;
    private String mSource;
    private Options mOptions;
    private JsFunction mCompleteCallback;
    private JsFunction mErrorCallback;
    private int mState = STATE_IDLE;

    public static class Options {
        public boolean loop = false;
        public float volume = 1.0f;
    }

    public AudioPlayer(Context context) {
        this(context, new Options());
    }

    public AudioPlayer(Context context, Options options) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mOptions = options;
        if (mOptions != null) {
            mPlayer.setLooping(mOptions.loop);
            mPlayer.setVolume(mOptions.volume, mOptions.volume);
        }
    }

    public void setCompleteCallback(int complete) {
        mCompleteCallback = (JsFunction) Memory.getObject(complete);
    }

    void notifyCompleteCallback() {
        if (mCompleteCallback != null) {
            mCompleteCallback.invoke();
        }
    }

    public void setErrorCallback(int error) {
        mErrorCallback = (JsFunction) Memory.getObject(error);
    }

    void notifyErrorCallback(String errMsg) {
        if (mErrorCallback != null) {
            mErrorCallback.invoke(errMsg);
        }
    }

    public int getState() {
        return mState;
    }

    @Override
    public void prepare(String url) {
        if (!URLUtil.isFileUrl(url) && !URLUtil.isNetworkUrl(url)) {
            notifyErrorCallback("invalid URL");
            return;
        }

        if (mState == STATE_IDLE) {
            mSource = url;
            try {
                mPlayer.setDataSource(mSource);
                mState = STATE_INITED;
            } catch (IOException e) {
                notifyErrorCallback(e.getMessage());
            }
        }

        if (mState == STATE_INITED
                || mState == STATE_STOPPED) {
            try {
                mPlayer.prepare();
                mState = STATE_PREPARED;
            } catch (IOException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
    }

    @Override
    public void start() {
        if (mState == STATE_PREPARED
                || mState == STATE_COMPLETED) {
            try {
                mPlayer.start();
                mState = STATE_STARTED;
            } catch (IllegalStateException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
    }

    @Override
    public void resume() {
        if (mState == STATE_PAUSED) {
            try {
                mPlayer.start();
                mState = STATE_STARTED;
            } catch (IllegalStateException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
    }

    @Override
    public void pause() {
        if (mState == STATE_STARTED) {
            try {
                mPlayer.pause();
                mState = STATE_PAUSED;
            } catch (IllegalStateException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        if (mState == STATE_STARTED
                || mState == STATE_PAUSED
                || mState == STATE_COMPLETED) {
            try {
                mPlayer.stop();
                mState = STATE_STOPPED;
            } catch (IllegalStateException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        mPlayer.reset();
        mState = STATE_IDLE;
    }

    public boolean seekTo(int msec) {
        if (mState == STATE_PREPARED
                || mState == STATE_STARTED
                || mState == STATE_PAUSED
                || mState == STATE_COMPLETED) {
            try {
                mPlayer.seekTo(msec);
                return true;
            } catch (IllegalStateException e) {
                notifyErrorCallback(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void release() {
        if (mCompleteCallback != null) {
            Memory.releaseObject(mCompleteCallback);
        }
        if (mErrorCallback != null) {
            Memory.releaseObject(mErrorCallback);
        }

        if (mPlayer != null) {
            mPlayer.release();
            mState = STATE_END;
        }
    }

    public int getCurrentPosition() {
        return mPlayer == null ? -1 : mPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mPlayer == null ? -1 : mPlayer.getDuration();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!mp.isLooping()) {
            mState = STATE_COMPLETED;
            notifyCompleteCallback();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mState = STATE_ERROR;
        notifyErrorCallback("what:" + what + " extra:" + extra);
        return false;
    }
}
