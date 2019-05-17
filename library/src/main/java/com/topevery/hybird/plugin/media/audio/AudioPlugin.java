package com.topevery.hybird.plugin.media.audio;

import android.content.Context;
import android.media.SoundPool;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.topevery.hybird.reflect.Memory;
import com.topevery.hybird.reflect.JsObject;
import com.topevery.hybird.utils.JsonUtils;

import java.io.File;

/**
 * @author wujie
 */
public class AudioPlugin {
    private Context context;

    public AudioPlugin(Context context) {
        this.context = context;
    }

    public int AudioRecorder_create(final String format, final String _options, final int _error) {
        if (format == null) {
            return Memory.NULL;
        }

        int pointer = Memory.createObject(new Memory.JsObjectFactory() {
            @Override
            public JsObject create() {
                Recorder recorder = null;
                if (!TextUtils.isEmpty(format)) {
                    Recorder.Options options = JsonUtils.fromJson(
                            _options, Recorder.Options.class);
                    if (options == null) {
                        options = new Recorder.Options();
                    }

                    if (format.equalsIgnoreCase(Recorder.FORMAT_MP3)) {
                        recorder = new Mp3Recorder(context, options);
                    } else if (format.equalsIgnoreCase(Recorder.FORMAT_WAV)) {
                        recorder = new WavRecorder(context, options);
                    }

                    if (recorder != null) {
                        recorder.setErrorCallback(_error);
                    }
                }
                return recorder;
            }
        });

        return pointer;
    }

    public void AudioRecorder_prepare(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.prepare(null);
        }
    }

    public void AudioRecorder_start(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.start();
        }
    }

    public void AudioRecorder_resume(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.resume();
        }
    }

    public void AudioRecorder_pause(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.pause();
        }
    }

    public void AudioRecorder_stop(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.stop();
        }
    }

    public void AudioRecorder_release(int pointer) {
        Memory.releaseObject(pointer);
    }

    public int AudioRecorder_getState(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            recorder.getState();
        }
        return 0;
    }

    public String AudioRecorder_getPath(int pointer) {
        Recorder recorder = (Recorder) Memory.getObject(pointer);
        if (recorder != null) {
            File file = recorder.getFile();
            if (file != null) {
                Uri uri = Uri.fromFile(file);
                return uri.toString();
            }
        }
        return null;
    }

    public int AudioPlayer_create(final String _options, final int _complete, final int _error) {
        int pointer = Memory.createObject(new Memory.JsObjectFactory() {
            @Override
            public JsObject create() {
                AudioPlayer.Options options = JsonUtils.fromJson(
                        _options, AudioPlayer.Options.class);
                if (options == null) {
                    options = new AudioPlayer.Options();
                }
                AudioPlayer player = new AudioPlayer(context, options);
                player.setCompleteCallback(_complete);
                player.setErrorCallback(_error);
                return player;
            }
        });

        return pointer;
    }

    public void AudioPlayer_prepare(int pointer, String url) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.prepare(url);
        }
    }

    public void AudioPlayer_start(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.start();
        }
    }

    public void AudioPlayer_resume(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.resume();
        }
    }

    public void AudioPlayer_pause(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.pause();
        }
    }

    public void AudioPlayer_stop(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.stop();
        }
    }

    public void AudioPlayer_seekTo(int pointer, int msec) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.seekTo(msec);
        }
    }

    public void AudioPlayer_reset(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            player.reset();
        }
    }

    public int AudioPlayer_getState(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            return player.getState();
        }
        return 0;
    }

    public int AudioPlayer_getDuration(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            return player.getDuration();
        }
        return -1;
    }

    public int AudioPlayer_getPosition(int pointer) {
        AudioPlayer player = (AudioPlayer) Memory.getObject(pointer);
        if (player != null) {
            return player.getCurrentPosition();
        }
        return -1;
    }

    public void AudioPlayer_release(int pointer) {
        Memory.releaseObject(pointer);
    }

    public void playSound(String url) {
        if (!URLUtil.isFileUrl(url)) {
            return;
        }

        String path = Uri.parse(url).getPath();

        SoundPool soundPool = SoundPoolManager.getSoundPool();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1);
                soundPool.setOnLoadCompleteListener(null);
            }
        });

        soundPool.load(path, 1);
    }

//    public int SoundPlayer_create(final String _options, int _error) {
//        final JsFunction error = (JsFunction) Memory.getObject(_error);
//        final int pointer = Memory.createObject(new Memory.JsObjectFactory() {
//            @Override
//            public JsObject create() {
//                SoundPlayer.Options options = JsonUtils.fromJson(
//                        _options, SoundPlayer.Options.class);
//                if (options == null) {
//                    options = new SoundPlayer.Options();
//                }
//                return new SoundPlayer(context, options);
//            }
//        });
//
//
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        player.setCallback(new SoundPlayer.Callback() {
//
//            @Override
//            public void onError(String errMsg) {
//                if (error != null) {
//                    error.invoke(errMsg);
//                    Memory.releaseObject(error);
//                }
//            }
//        });
//
//        return pointer;
//    }
//
//    public void SoundPlayer_prepare(int pointer, String url) {
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        if (player != null) {
//            player.prepare(url);
//        }
//    }
//
//    public void SoundPlayer_start(int pointer) {
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        if (player != null) {
//            player.start();
//        }
//    }
//
//    public void SoundPlayer_resume(int pointer) {
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        if (player != null) {
//            player.resume();
//        }
//    }
//
//    public void SoundPlayer_pause(int pointer) {
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        if (player != null) {
//            player.pause();
//        }
//    }
//
//    public void SoundPlayer_stop(int pointer) {
//        SoundPlayer player = (SoundPlayer) Memory.getObject(pointer);
//        if (player != null) {
//            player.stop();
//        }
//    }
//
//    public void SoundPlayer_release(int pointer) {
//        Memory.releaseObject(pointer);
//    }
}
