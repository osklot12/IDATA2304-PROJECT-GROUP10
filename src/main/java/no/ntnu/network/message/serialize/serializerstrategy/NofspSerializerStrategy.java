package no.ntnu.network.message.serialize.serializerstrategy;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.bytedeserialized.ByteDeserializedInteger;
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
    private static final byte[] INTEGER_BYTES = new byte[] {0, 0};


    @Override
    public ByteDeserializable serializeInteger(int integer) {
        byte[] lengthField = null;
        byte[] valueBytes = intToBytes(integer, TYPE_FIELD_LENGTH);
        if (valueBytes != null) {
            lengthField = intToBytes(valueBytes.length, LENGTH_FIELD_LENGTH);
        } else {
            throw new SerializationException("Cannot serialize, because value field is empty.");
        }

        ByteBuffer buffer = new ByteBuffer();
        buffer.addBytes(INTEGER_BYTES, lengthField, valueBytes);

        return new ByteSerializedInteger(buffer.toArray());
    }

    @Override
    public ByteSerializable deserialize(ByteDeserializable deserializable) throws SerializationException {
        if (deserializable == null) {
            throw new SerializationException("Cannot deserialize, because ByteDeserializable is null.");
        }

        ByteSerializable serializable = null;

        byte[] bytes = deserializable.getBytes();
        byte[] typeBytes = Arrays.copyOfRange(bytes, 0, TYPE_FIELD_LENGTH);
        if (Arrays.equals(typeBytes, INTEGER_BYTES)) {
            serializable = deserializeInteger(bytes);
        }

        return serializable;
    }

    private ByteSerializable deserializeInteger(byte[] bytes) {
        byte[] valueField = getValueField(bytes);

        int integer = bytesToInt(valueField);

        return new ByteDeserializedInteger(integer);
    }

    private static byte[] getValueField(byte[] bytes) {
        return Arrays.copyOfRange(bytes, TYPE_FIELD_LENGTH + LENGTH_FIELD_LENGTH, bytes.length);
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

    public static int bytesToInt(byte[] bits) {
        int result = 0;
        for (byte b : bits) {
            result = (result << 1) + b;
        }
        return result;
    }

    private byte[] bytesTlv(byte[] type, byte[] length, byte[] value) {
        return null;
    }
}
