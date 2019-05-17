package com.topevery.hybird.plugin.media.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.topevery.hybird.utils.ShortBuffer;
import com.topevery.hybird.utils.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wujie
 */
public class Mp3Recorder extends Recorder {
    private AudioRecord mRecord;

    private final ShortBuffer mBuffer =
            new ShortBuffer(4096);

    final int mQuality = 7;
    final int mPeriodInFrames = 160;

    private int mBufferSize;
    private int mOutputBufferSize;

    private RecordThread mRecordThread;
    private EncodeThread mEncodeThread;

    private Lame mLame = new Lame();
    private File mFile;

    private int mState = STATE_IDLE;

    public Mp3Recorder(Context context, Options options) {
        super(context, options);
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void prepare(String source) {
        if (mState == STATE_IDLE || mState == STATE_STOPPED) {
            final int sampleRate = mOptions.sampleRate;
            final int bitPerSample = mOptions.bitPerSample;
            final int channelConfig = mOptions.getChannelConfig();
            final int audioFormat = mOptions.getAudioFormat();
            final int encodeBitRate = mOptions.getEncodeBitRate();

            mBufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    channelConfig,
                    audioFormat);

            if (mBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                mState = STATE_ERROR;
                errorMessage("error bad value.");
                return;
            }
            if (mBufferSize == AudioRecord.ERROR) {
                mState = STATE_ERROR;
                errorMessage("error.");
                return;
            }

            mBufferSize /= (bitPerSample / 8);
            if (mBufferSize % mPeriodInFrames != 0) {
                mBufferSize += (mPeriodInFrames - mBufferSize % mPeriodInFrames);
            }
            mBufferSize *= (bitPerSample / 8);
            mOutputBufferSize = (int) (7200 + (mBufferSize * 2 * 1.25));

            mLame.init(channelConfig, sampleRate, sampleRate, encodeBitRate, mQuality);

            try {
                mRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        mOptions.sampleRate,
                        channelConfig,
                        audioFormat,
                        mBufferSize);
                mState = STATE_PREPARED;
            } catch (IllegalArgumentException e) {
                mState = STATE_ERROR;
                errorMessage("Invalid parameter.");
            }
        }
    }

    @Override
    public void start() {
        if (mState == STATE_PREPARED) {
            @SuppressLint("SimpleDateFormat")
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String name = "REC_" + dateFormat.format(new Date()) + ".mp3";
            File parent = Storage.getRecordDir(getContext());
            mFile = new File(parent, name);
            try {
                mEncodeThread = new EncodeThread(mFile);
                mRecord.startRecording();
                mRecord.setRecordPositionUpdateListener(mEncodeThread);
                mRecord.setPositionNotificationPeriod(mPeriodInFrames);

                mRecordThread = new RecordThread();
                mRecordThread.start();
                mState = STATE_STARTED;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resume() {
        if (mState == STATE_PAUSED) {
            if (mRecordThread != null) {
                mRecordThread.resumeThread();
            }
            if (mEncodeThread != null) {
                mEncodeThread.resumeThread();
            }
            mState = STATE_STARTED;
        }
    }

    @Override
    public void pause() {
        if (mState == STATE_STARTED) {
            if (mRecordThread != null) {
                mRecordThread.pauseThread();
            }
            if (mEncodeThread != null) {
                mEncodeThread.pauseThread();
            }
            mState = STATE_PAUSED;
        }
    }

    @Override
    public void stop() {
        if (mState == STATE_STARTED || mState == STATE_PAUSED) {
            mEncodeThread.pauseThread();
            if (mRecordThread != null && mRecordThread.isAlive()) {
                mRecordThread.setRunning(false);
                mRecordThread.interrupt();
                mRecordThread = null;
            }
            mEncodeThread.flush();
            mEncodeThread.close();
            mRecord.stop();
            mState = STATE_STOPPED;
        }
    }

    @Override
    public void reset() {
        if (mState == STATE_ERROR) {
            mState = STATE_IDLE;
        }
    }

    @Override
    public void release() {
        super.release();
        if (mLame != null) {
            mLame.close();
        }
        if (mRecord != null) {
            mRecord.release();
        }
        mState = STATE_END;
    }

    public File getFile() {
        return mFile;
    }

    class RecordThread extends Thread {
        final short[] buffer;
        private volatile boolean running = true;
        private volatile boolean writing = true;

        public RecordThread() {
            this.buffer = new short[mBufferSize];
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(
                    android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            while(running) {
                int length = mRecord.read(buffer, 0, mBufferSize);
                if (length > 0) {
                    if (writing) {
                        mBuffer.write(buffer, 0, length);
                    }
                }
            }
        }

        public short[] getBuffer() {
            return buffer;
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public void resumeThread() {
            this.writing = true;
        }

        public void pauseThread() {
            this.writing = false;
        }
    }

    class EncodeThread implements AudioRecord.OnRecordPositionUpdateListener {
        final short[] buffer;
        final byte[] outputBuffer;

        private volatile boolean encoding = true;

        final OutputStream stream;

        public EncodeThread(File outfile) throws FileNotFoundException {
            this.buffer = new short[mBufferSize];
            this.outputBuffer = new byte[mOutputBufferSize];
            this.stream = new FileOutputStream(outfile);
        }

        @Override
        public void onMarkerReached(AudioRecord recorder) {

        }

        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            if (this.encoding) {
                int length = onPeriodicEncode();
                Log.i("Mp3Recorder", "onPeriodicEncode:" + length);
            }
        }

        int onPeriodicEncode() {
            int length = mBuffer.read(buffer, 0, mBufferSize);
            if (length > 0) {
                int length2 = mLame.encode(buffer, buffer, length, outputBuffer, mOutputBufferSize);
                if (length2 > 0) {
                    try {
                        stream.write(outputBuffer, 0, length2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return length2;
            }
            return 0;
        }

        public void resumeThread() {
            this.encoding = true;
        }

        public void pauseThread() {
            this.encoding = false;
        }

        public void flush() {
            int length2 = mLame.flush(outputBuffer, mOutputBufferSize);
            if (length2 > 0) {
                try {
                    stream.write(outputBuffer, 0, length2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
