package no.ntnu.network.message.serialize.tool;

import java.io.IOException;

/**
 * A buffer for handling bytes. The {@code SimpleByteBuffer} provides efficient operations on the buffer,
 * having adding and removing bytes run in O(n) time.
 */
public class SimpleByteBuffer implements ByteSource {
    private static final int DEFAULT_INITIAL_CAPACITY = 32;
    private byte[] buffer;
    private int head;
    private int tail;

    /**
     * Creates a new SimpleByteBuffer.
     */
    public SimpleByteBuffer() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new SimpleByteBuffer with a predefined initial capacity.
     *
     * @param initialCapacity initial buffer capacity
     */
    public SimpleByteBuffer(int initialCapacity) {
        this.buffer = new byte[initialCapacity];

        this.head = 0;
        this.tail = 0;
    }

    /**
     * Creates a new SimpleByteBuffer.
     *
     * @param bytes bytes to add to buffer
     */
    public SimpleByteBuffer(byte[] bytes) {
        this(bytes.length);

        addBytes(bytes);
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

    /**
     * Expands the underlying array size if necessary.
     */
    private void handleBufferExpansion() {
        if (tail == buffer.length) {
            buffer = migrateBytes(buffer.length * 2);
        }
    }

    /**
     * Reduces the underlying array size if necessary.
     */
    private void handleBufferReduction() {
        int currentSize = size();
        if (currentSize <= buffer.length / 4 && buffer.length > 32) {
            buffer = migrateBytes(Math.max(currentSize * 2, 32));
            head = 0;
            tail = currentSize;
        }
    }

    /**
     * Migrates the bytes in the underlying array to a new array of a new size.
     *
     * @param newSize the size of the new array
     * @return the new array
     */
    private byte[] migrateBytes(int newSize) {
        byte[] newBytes = new byte[newSize];
        System.arraycopy(buffer, head, newBytes, 0, size());

        return newBytes;
    }

    public void reset() {
        this.buffer = new byte[DEFAULT_INITIAL_CAPACITY];
        head = 0;
        tail = 0;
    }

    /**
     * Returns the size of the buffer.
     *
     * @return buffer size
     */
    public int size() {
        return tail - head;
    }

    /**
     * Returns the buffer as an array.
     *
     * @return buffer as array
     */
    public byte[] toArray() {
        return migrateBytes(size());
    }

    @Override
    public int read() throws IOException {
        int readByte = -1;

        if (head < tail) {
            readByte = buffer[head++] & 0xFF;
            handleBufferReduction();
        }

        return readByte;
    }

    @Override
    public String toString() {
        return ByteHandler.bytesToString(toArray());
    }
}
