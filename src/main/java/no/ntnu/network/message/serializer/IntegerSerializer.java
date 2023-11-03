package no.ntnu.network.message.serializer;

public class IntegerSerializer {
    /**
     * Serializes an integer to an array of bytes.
     *
     * @param value integer to serialize
     * @param length the length
     * @return
     */
    public static byte[] toBytes(int value, int length) {
        long maxValue = (1L << (length * 8)) - 1;

        if (value < 0 || value > maxValue) {
            return null;
        }

        byte[] byteArray = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            byteArray[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return byteArray;
    }
}
