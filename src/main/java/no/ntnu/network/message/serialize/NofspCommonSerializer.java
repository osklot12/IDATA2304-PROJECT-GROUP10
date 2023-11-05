package no.ntnu.network.message.serialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.ByteHandler;

import java.nio.ByteBuffer;

/**
 * A serializer that handles serialization of common data such as integers, lists etc...
 */
public class NofspCommonSerializer {
    public static byte[] serializeInteger(ByteSerializableInteger integer) throws SerializationException {
        byte[] lengthField = null;

        byte[] valueBytes = intToBytes(integer.getInteger());
        if (valueBytes != null) {
            lengthField = intToFixedLengthBytes(valueBytes.length, NofspSerializationConstants.LENGTH_FIELD_LENGTH);
        } else {
            throw new SerializationException("Cannot serialize integer, because value field is empty.");
        }

        SimpleByteBuffer tlv = new SimpleByteBuffer();
        tlv.addBytes(NofspSerializationConstants.INTEGER_BYTES, lengthField, valueBytes);

        return tlv.toArray();
    }

    private static byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(value);
        return ByteHandler.dynamicLengthBytes(buffer.array());
    }

    private static byte[] intToFixedLengthBytes(int value, int length) throws SerializationException {
        byte[] intInBytes = intToBytes(value);

        // integer representation in bytes is longer than the desired length
        if (intInBytes.length > length) {
            throw new SerializationException("Cannot represent integer " + value + " with " + length + " bytes," +
                    "because the value requires more bytes.");
        }

        // integer representation in bytes is smaller than the desired length
        if (intInBytes.length < length) {
            try {
                intInBytes = ByteHandler.addLeadingPadding(intInBytes, length);
            } catch (IllegalArgumentException e) {
                throw new SerializationException("Cannot translate integer " + value + " into bytes of length " +
                        length + ": " + e.getMessage());
            }
        }

        return intInBytes;
    }
}
