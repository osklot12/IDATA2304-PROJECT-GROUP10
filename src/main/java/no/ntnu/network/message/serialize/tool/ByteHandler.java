package no.ntnu.network.message.serialize.tool;

import java.util.Arrays;

/**
 * A class handling arrays of bytes.
 */
public class ByteHandler {
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
}
