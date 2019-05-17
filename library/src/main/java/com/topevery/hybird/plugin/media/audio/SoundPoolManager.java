package com.topevery.hybird.plugin.media.audio;

import android.media.SoundPool;
import android.media.AudioManager;

/**
 * @author wujie
 */
public class SoundPoolManager {

    private static SoundPool soundPool = null;

    public static SoundPool getSoundPool() {
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        return soundPool;
    }
}
