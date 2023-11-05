package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.ByteHandler;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A deserializer constructing {@code ByteSerializable} objects from arrays of bytes.
 * The deserializer follows the technique described by NOFSP.
 */
public class NofspCommonDeserializer {
    private NofspCommonDeserializer() {

    }

    /**
     * Deserializes an array of bytes.
     *
     * @param bytes bytes to deserialize
     * @return a {@code ByteSerializable} object
     * @throws SerializationException thrown when bytes cannot be deserialized
     */
    public static ByteSerializable deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            throw new SerializationException("Cannot identify type field, because bytes is null.");
        }

        ByteSerializable serializable = null;

        // type field: integer
        if (Arrays.equals(getTypeField(bytes), NofspSerializationConstants.INTEGER_BYTES)) {
            serializable = getInteger(getValueField(bytes));
        }

        return serializable;
    }

    public static byte[] getTypeField(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 0, NofspSerializationConstants.TYPE_FIELD_LENGTH);
    }

    public static byte[] getLengthField(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 0, NofspSerializationConstants.LENGTH_FIELD_LENGTH);
    }

    public static byte[] getValueField(byte[] bytes) {
        int valueStartIndex = NofspSerializationConstants.TYPE_FIELD_LENGTH + NofspSerializationConstants.LENGTH_FIELD_LENGTH;
        int valueEndIndex = bytes.length;

        return Arrays.copyOfRange(bytes, valueStartIndex, valueEndIndex);
    }

    public static ByteSerializableInteger getInteger(byte[] bytes) {
        return new ByteSerializableInteger(bytesToInt(getValueField(bytes)));
    }

    private static int bytesToInt(byte[] bytes) throws SerializationException {
        byte[] bytesToConvert = bytes;

        // removes any leading padding for the bytes
        bytesToConvert = ByteHandler.dynamicLengthBytes(bytes);

        if (bytesToConvert.length < Integer.BYTES) {
            try {
                bytesToConvert = ByteHandler.addLeadingPadding(bytesToConvert, Integer.BYTES);
            } catch (IllegalArgumentException e) {
                throw new SerializationException("Cannot convert bytes to integer: " + e.getMessage());
            }
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytesToConvert);
        return buffer.getInt();
    }
}
