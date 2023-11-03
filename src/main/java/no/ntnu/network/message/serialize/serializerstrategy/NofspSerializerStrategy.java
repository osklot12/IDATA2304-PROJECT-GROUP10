package no.ntnu.network.message.serialize.serializerstrategy;

import no.ntnu.network.message.common.byteserialized.ByteSerializedInteger;
import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.ByteSerializable;

import java.util.Arrays;

/**
 * A serializer strategy implementing the data marshalling technique described by NOFSP.
 * The strategy used TLV recursively for defining data, with 2-byte long type field and 4-byte long length field.
 * This allows for 65536 different types, and a maximum value length of 4 GB.
 */
public class NofspSerializerStrategy implements SerializerStrategy {
    // length constants
    private static final int TYPE_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_LENGTH = 4;

    // byte constants
    private static final byte[] INTEGER_BYTE = new byte[] {0, 0};


    @Override
    public ByteDeserializable serializeInteger(int integer) {
        byte[] bytes = intToBytes(integer);

        return new ByteSerializedInteger()
    }

    @Override
    public ByteSerializable deserialize(byte[] bytes) {
        byte[] typeBytes = Arrays.copyOfRange(bytes, 0, TYPE_FIELD_LENGTH - 1);

        if ()
    }

    private static byte[] intToBytes(int value, int length) {
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

    private byte[] bytesTlv(byte[] type, byte[] length, byte[] value) {
        return null;
    }

    @Override
    public ByteDeserializable serializeList(ByteSerializableList list) {
        return null;
    }
}
