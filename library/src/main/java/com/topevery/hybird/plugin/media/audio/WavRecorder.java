package com.topevery.hybird.plugin.media.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.topevery.hybird.utils.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wujie
 */
public class WavRecorder extends Recorder {
    private AudioRecord mRecord;
    private RecordThread mRecordThread;

    private int mBufferSize;
    private File mFile;

    private int mState = STATE_IDLE;

    public WavRecorder(Context context, Options options) {
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
            final int channelConfig = mOptions.getChannelConfig();
            final int audioFormat = mOptions.getAudioFormat();

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

            try {
                mRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
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
            String name = "REC_" + dateFormat.format(new Date()) + ".wav";
            File parent = Storage.getRecordDir(getContext());
            mFile = new File(parent, name);
            try {
                mRecord.startRecording();
                mRecordThread = new RecordThread(mBufferSize, mFile);
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
                mState = STATE_STARTED;
            }
        }
    }

    @Override
    public void pause() {
        if (mState == STATE_STARTED) {
            if (mRecordThread != null) {
                mRecordThread.pauseThread();
                mState = STATE_PAUSED;
            }
        }
    }

    @Override
    public void stop() {
        if (mState == STATE_STARTED || mState == STATE_PAUSED) {
            if (mRecordThread != null && mRecordThread.isAlive()) {
                mRecordThread.setRunning(false);
                mRecordThread.interrupt();
                mRecordThread = null;
            }
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
        if (mRecord != null) {
            mRecord.release();
        }
        mState = STATE_END;
    }

    public File getFile() {
        return mFile;
    }

    class RecordThread extends Thread {
        final int bufferSize;
        final byte[] buffer;
        final RandomAccessFile stream;

        private volatile boolean running = true;
        private volatile boolean writing = true;

        public RecordThread(int bufferSize, File outfile) throws FileNotFoundException {
            this.bufferSize = bufferSize;
            this.buffer = new byte[bufferSize];
            this.stream = new RandomAccessFile(outfile, "rw");
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(
                    android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int fileSize = 0;
            try {
                stream.seek(44);
                while(running) {
                    int length = mRecord.read(buffer, 0, bufferSize);
                    if (length > 0) {
                        try {
                            if (writing) {
                                stream.write(buffer, 0, length);
                                fileSize += length;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                writeFinally(fileSize);
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void writeFinally(int fileSize) {
            final int sampleRate = mOptions.sampleRate;
            final int bitPerSample = mOptions.bitPerSample;
            final int channels = mOptions.channels;
            try {
                stream.seek(0);
                stream.writeBytes("RIFF");
                stream.writeInt(Integer.reverseBytes(fileSize + 36));
                stream.writeBytes("WAVE");
                stream.writeBytes("fmt ");
                stream.writeInt(Integer.reverseBytes(16));
                stream.writeShort(Short.reverseBytes((short) 1));
                stream.writeShort(Short.reverseBytes((short) channels));
                stream.writeInt(Integer.reverseBytes(sampleRate));
                stream.writeInt(Integer.reverseBytes(sampleRate * bitPerSample * channels / 8));
                stream.writeShort(Short.reverseBytes((short) (bitPerSample * channels / 8)));
                stream.writeShort(Short.reverseBytes((short) bitPerSample));
                stream.writeBytes("data");
                stream.writeInt(Integer.reverseBytes(fileSize));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
