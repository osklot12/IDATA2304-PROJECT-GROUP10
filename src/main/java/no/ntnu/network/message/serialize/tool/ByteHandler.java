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
     */
    public static int bytesToInt(byte[] bytes) {
        // removes any leading padding for the bytes
        bytes = ByteHandler.dynamicLengthBytes(bytes);

        if (bytes.length < Integer.BYTES) {
            try {
                bytes = ByteHandler.addLeadingPadding(bytes, Integer.BYTES);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot convert bytes to integer: " + e.getMessage());
            }
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    /**
     * Converts an array of bytes to a String.
     *
     * @param bytes bytes to convert
     * @return bytes represented by a String
     */
    public static String bytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int n = 16; // bytes per line

        int byteCounter = 1;
        for (byte b : bytes) {
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            sb.append(binaryString).append(" ");

            // creates a new line every n bytes
            if (byteCounter % n == 0) {
                sb.append(System.lineSeparator());
            }
            byteCounter++;
        }

        return sb.toString().trim();
    }

    /**
     * Combines multiple arrays of bytes into one array of bytes.
     * The arrays are inserted into a single array in the same order they are given.
     *
     * @param byteArrays arrays to combine
     * @return all given arrays in one array
     */
    public static byte[] combineBytes(byte[]... byteArrays) {
        byte[] result = new byte[0];

        for (byte[] bytes : byteArrays) {
            result = Arrays.copyOf(result, result.length + bytes.length);
            System.arraycopy(bytes, 0, result, result.length - bytes.length, bytes.length);
        }

        return result;
    }
}
