package no.ntnu.network.message.serialize.serializerstrategy;

/**
 * A buffer for holding bytes.
 * The buffer automatically increases when bytes are added, and automatically shrinks when bytes are removed.
 */
public class ByteBuffer {
    private byte[] buffer;
    private int tail;

    /**
     * Creates a new ByteBuffer.
     */
    public ByteBuffer() {
        this.buffer = new byte[32];
        this.tail = 0;
    }

    /**
     * Adds a single byte to the buffer.
     *
     * @param aByte byte to add
     */
    public void addByte(byte aByte) {
            buffer[tail] = aByte;
            tail++;

            handleBufferExpansion();
    }

    private void handleBufferExpansion() {
        if (tail > buffer.length/2) {
            buffer = migrateBytes(buffer, buffer.length * 2);
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
     * Returns the buffer as an array
     *
     * @return buffer as array
     */
    public byte[] toArray() {
        return migrateBytes(buffer, tail);
    }
}
