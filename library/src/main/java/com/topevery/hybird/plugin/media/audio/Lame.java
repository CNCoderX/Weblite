package com.topevery.hybird.plugin.media.audio;

/**
 * @author wujie
 */
public class Lame {
    static {
        System.loadLibrary("lame");
    }

    public native void init(int channels, int inSampleRate, int outSampleRate, int encodeBitRate, int quality);

    public native int encode(short[] lBuffer, short[] rBuffer, int samples, byte[] outBuffer, int outBufferSize);

    public native int flush(byte[] outBuffer, int outBufferSize);

    public native void close();
}
