package com.topevery.hybird.js_interface;

import android.content.Context;

import com.topevery.hybird.plugin.media.audio.AudioPlugin;
import com.topevery.hybird.plugin.media.image.ImagePlugin;
import com.topevery.hybird.plugin.media.video.VideoPlugin;

import org.xwalk.core.JavascriptInterface;

/**
 * @author wujie
 */
public class MediaInterface {
    private final ImagePlugin mImagePlugin;
    private final AudioPlugin mAudioPlugin;
    private final VideoPlugin mVideoPlugin;

    public MediaInterface(Context context) {
        mImagePlugin = new ImagePlugin(context);
        mAudioPlugin = new AudioPlugin(context);
        mVideoPlugin = new VideoPlugin(context);
    }

    @JavascriptInterface
    public void previewImage(String url) {
        mImagePlugin.previewImage(url);
    }

    @JavascriptInterface
    public void previewImages(int current, String urls) {
        mImagePlugin.previewImages(current, urls);
    }

    @JavascriptInterface
    public void pickImage(String options, int success, int failure) {
        mImagePlugin.pickImage(options, success, failure);
    }

    @JavascriptInterface
    public void chooseImages(String options, int success, int failure) {
        mImagePlugin.chooseImages(options, success, failure);
    }

    @JavascriptInterface
    public int AudioRecorder_create(String format, String options, int error) {
        return mAudioPlugin.AudioRecorder_create(format, options, error);
    }

    @JavascriptInterface
    public void AudioRecorder_prepare(int pointer) {
        mAudioPlugin.AudioRecorder_prepare(pointer);
    }

    @JavascriptInterface
    public void AudioRecorder_start(int pointer) {
        mAudioPlugin.AudioRecorder_start(pointer);
    }

    @JavascriptInterface
    public void AudioRecorder_resume(int pointer) {
        mAudioPlugin.AudioRecorder_resume(pointer);
    }

    @JavascriptInterface
    public void AudioRecorder_pause(int pointer) {
        mAudioPlugin.AudioRecorder_pause(pointer);
    }

    @JavascriptInterface
    public void AudioRecorder_stop(int pointer) {
        mAudioPlugin.AudioRecorder_stop(pointer);
    }

    @JavascriptInterface
    public void AudioRecorder_release(int pointer) {
        mAudioPlugin.AudioRecorder_release(pointer);
    }

    @JavascriptInterface
    public int AudioRecorder_getState(int pointer) {
        return mAudioPlugin.AudioRecorder_getState(pointer);
    }

    @JavascriptInterface
    public String AudioRecorder_getPath(int pointer) {
        return mAudioPlugin.AudioRecorder_getPath(pointer);
    }

    @JavascriptInterface
    public int AudioPlayer_create(String options, int success, int failure) {
        return mAudioPlugin.AudioPlayer_create(options, success, failure);
    }

    @JavascriptInterface
    public void AudioPlayer_prepare(int pointer, String url) {
        mAudioPlugin.AudioPlayer_prepare(pointer, url);
    }

    @JavascriptInterface
    public void AudioPlayer_start(int pointer) {
        mAudioPlugin.AudioPlayer_start(pointer);
    }

    @JavascriptInterface
    public void AudioPlayer_resume(int pointer) {
        mAudioPlugin.AudioPlayer_resume(pointer);
    }

    @JavascriptInterface
    public void AudioPlayer_pause(int pointer) {
        mAudioPlugin.AudioPlayer_pause(pointer);
    }

    @JavascriptInterface
    public void AudioPlayer_stop(int pointer) {
        mAudioPlugin.AudioPlayer_stop(pointer);
    }

    @JavascriptInterface
    public void AudioPlayer_seekTo(int pointer, int msec) {
        mAudioPlugin.AudioPlayer_seekTo(pointer, msec);
    }

    @JavascriptInterface
    public void AudioPlayer_reset(int pointer) {
        mAudioPlugin.AudioPlayer_reset(pointer);
    }

    @JavascriptInterface
    public int AudioPlayer_getState(int pointer) {
        return mAudioPlugin.AudioPlayer_getState(pointer);
    }

    @JavascriptInterface
    public int AudioPlayer_getDuration(int pointer) {
        return mAudioPlugin.AudioPlayer_getDuration(pointer);
    }

    @JavascriptInterface
    public int AudioPlayer_getPosition(int pointer) {
        return mAudioPlugin.AudioPlayer_getPosition(pointer);
    }

    @JavascriptInterface
    public void AudioPlayer_release(int pointer) {
        mAudioPlugin.AudioPlayer_release(pointer);
    }

//    @JavascriptInterface
//    public int SoundPlayer_create(String options, int error) {
//        return mAudioPlugin.SoundPlayer_create(options, error);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_prepare(int pointer, String url) {
//        mAudioPlugin.SoundPlayer_prepare(pointer, url);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_start(int pointer) {
//        mAudioPlugin.SoundPlayer_start(pointer);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_resume(int pointer) {
//        mAudioPlugin.SoundPlayer_resume(pointer);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_pause(int pointer) {
//        mAudioPlugin.SoundPlayer_pause(pointer);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_stop(int pointer) {
//        mAudioPlugin.SoundPlayer_stop(pointer);
//    }
//
//    @JavascriptInterface
//    public void SoundPlayer_release(int pointer) {
//        mAudioPlugin.SoundPlayer_release(pointer);
//    }

    @JavascriptInterface
    public void playSound(String url) {
        mAudioPlugin.playSound(url);
    }

    @JavascriptInterface
    public void previewVideo(String url) {
        mVideoPlugin.previewVideo(url);
    }

    @JavascriptInterface
    public void pickVideo(String source, int success, int failure) {
        mVideoPlugin.pickVideo(source, success, failure);
    }
}
