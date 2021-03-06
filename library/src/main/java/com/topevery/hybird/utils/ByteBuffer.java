package com.topevery.hybird.utils;

import java.util.Arrays;

/**
 * @author wujie
 */
public class ByteBuffer {
    private byte[] mBuffer;
    private int capacity;
    private int position;

    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public ByteBuffer() {
        this(1024);
    }

    public ByteBuffer(int capacity) {
        this.capacity = capacity;
        mBuffer = new byte[capacity];
    }

    public synchronized int read(byte[] bytes, int offset, int length) {
        int position = this.position;
        if (position <= 0) {
            return -1;
        }
        int readCount = Math.min(position, length);
        int remaining = position - readCount;
        System.arraycopy(mBuffer, 0, bytes, offset, readCount);
        if (remaining > 0) {
            System.arraycopy(mBuffer, readCount, mBuffer, 0, remaining);
            this.position = remaining;
        } else {
            this.position = 0;
        }
        return readCount;
    }

    public synchronized void write(byte[] bytes, int offset, int length) {
        int position = this.position;
        int remaining = capacity - position;
        if (remaining < length) {
            grow(position + length);
        }
        System.arraycopy(bytes, offset, mBuffer, position, length);
        this.position += length;
    }

    private void grow(int minCapacity) {
        int oldCapacity = capacity;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            throw new OutOfMemoryError();
        }
        this.capacity = newCapacity;
        mBuffer = Arrays.copyOf(mBuffer, newCapacity);
    }
}
