package no.ntnu.network.message.serialize.tool;

import java.io.InputStream;

/**
 * A buffer for holding bytes.
 * The buffer automatically increases when bytes are added, and automatically shrinks when bytes are removed.
 */
public class SimpleByteBuffer {
    private byte[] buffer;
    private int tail;

    /**
     * Creates a new SimpleByteBuffer.
     */
    public SimpleByteBuffer() {
        this.buffer = new byte[32];
        this.tail = 0;
    }

    /**
     * Creates a new SimpleByteBuffer.
     *
     * @param bytes bytes to add to buffer
     */
    public SimpleByteBuffer(byte[] bytes) {
        this();

        addBytes(bytes);
    }

    /**
     * Creates a new SimpleByteBuffer.
     *
     * @param inputStream input stream to buffer bytes for
     */
    public SimpleByteBuffer(InputStream inputStream) {
        this();

    }

    /**
     * Adds a single byte to the buffer.
     *
     * @param aByte byte to add
     */
    public void addByte(byte aByte) {
            handleBufferExpansion();
            buffer[tail] = aByte;
            tail++;
    }

    private void handleBufferExpansion() {
        if (tail == buffer.length) {
            buffer = migrateBytes(buffer, buffer.length + 32);
        }
    }

    /**
     * Adds an array of bytes to the buffer.
     *
     * @param bytes array of bytes
     */
    public void addBytes(byte[]... bytes) {
        for (byte[] byteArray : bytes) {
            for (byte aByte : byteArray) {
                addByte(aByte);
            }
        }
    }

    private byte[] migrateBytes(byte[] oldBytes, int newSize) {
        byte[] newBytes = oldBytes;

        if (newSize > oldBytes.length) {
            newBytes = new byte[newSize];
            System.arraycopy(oldBytes, 0, newBytes, 0, oldBytes.length);

        } else if (newSize < oldBytes.length) {
            newBytes = new byte[newSize];
            System.arraycopy(oldBytes, 0, newBytes, 0, newBytes.length);

        }

        return newBytes;
    }

    /**
     * Returns the capacity of the buffer.
     *
     * @return capacity of buffer
     */
    public int getCapacity() {
        return buffer.length;
    }

    /**
     * Returns the buffer as an array.
     *
     * @return buffer as array
     */
    public byte[] toArray() {
        return migrateBytes(buffer, tail);
    }
}
