package no.ntnu.network.message.serialize.tool;

import no.ntnu.exception.SerializationException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A class handling arrays of bytes.
 * The {@code ByteHandler} is responsible for translating primitive datatype into bytes and arranging the
 * representation.
 */
public class ByteHandler {
    private ByteHandler() {

    }

    /**
     * Removes padding (leading 0's) from any given array of bytes.
     *
     * @param bytes bytes to dynamically crop
     * @return cropped array of bytes, only containing actual information
     */
    public static byte[] dynamicLengthBytes(byte[] bytes) {
        if (bytes[0] != 0 || bytes.length == 1) {
            return bytes;
        }

        return dynamicLengthBytes(Arrays.copyOfRange(bytes, 1, bytes.length));
    }

    /**
     * Translates an integer into an array of bytes.
     *
     * @param value the integer to translate
     * @return array of bytes representing the integer
     */
    public static byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(value);

        return dynamicLengthBytes(buffer.array());
    }

    /**
     * Adds leading padding to a given array of bytes, to give the resulting array of bytes a particular length.
     *
     * @param bytes bytes to add padding to
     * @param totalLength the total length of the resulting array, cannot be smaller than the length of the given array
     *                    of bytes
     * @return given bytes with added padding
     * @throws IllegalArgumentException thrown if desired length is shorter than the length of the given bytes
     */
    public static byte[] addLeadingPadding(byte[] bytes, int totalLength) throws IllegalArgumentException {
        if (totalLength < bytes.length) {
            throw new IllegalArgumentException("Cannot create array of bytes with length " + totalLength + ", because" +
                    " the length of the given array of bytes is longer.");
        }

        byte[] result = bytes;

        int paddingLength = totalLength - bytes.length;

        if (paddingLength > 0) {
            SimpleByteBuffer simpleByteBuffer = new SimpleByteBuffer();

            byte[] padding = new byte[paddingLength];

            simpleByteBuffer.addBytes(padding, bytes);

            result = simpleByteBuffer.toArray();
        }


        return result;
    }

    /**
     * Translates an array of bytes into an integer.
     *
     * @param bytes bytes to translate
     * @return translated integer
     * @throws SerializationException thrown when bytes cannot be converted into integer
     */
    public static int bytesToInt(byte[] bytes) throws SerializationException {
        // removes any leading padding for the bytes
        bytes = ByteHandler.dynamicLengthBytes(bytes);

        if (bytes.length < Integer.BYTES) {
            try {
                bytes = ByteHandler.addLeadingPadding(bytes, Integer.BYTES);
            } catch (IllegalArgumentException e) {
                throw new SerializationException("Cannot convert bytes to integer: " + e.getMessage());
            }
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }
}
