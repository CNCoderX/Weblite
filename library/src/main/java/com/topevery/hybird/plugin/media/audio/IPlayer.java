package com.topevery.hybird.plugin.media.audio;

/**
 * @author wujie
 */
public interface IPlayer {
    void prepare(String source);
    void start();
    void resume();
    void pause();
    void stop();
    void reset();

    public static final int STATE_IDLE = 0;
    public static final int STATE_INITED = 1;
    public static final int STATE_PREPARED= 2;
    public static final int STATE_STARTED = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_STOPPED = 5;
    public static final int STATE_COMPLETED = 6;
    public static final int STATE_END = 7;
    public static final int STATE_ERROR = 8;
}
