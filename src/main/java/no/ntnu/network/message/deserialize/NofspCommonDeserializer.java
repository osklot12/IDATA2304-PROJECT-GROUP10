package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.common.byteserializable.ByteSerializableList;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.TlvFrame;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.util.Arrays;

/**
 * A deserializer constructing {@code ByteSerializable} objects from arrays of bytes.
 * The deserializer follows the technique described by NOFSP.
 */
public class NofspCommonDeserializer {
    private static final TlvFrame TLV_FRAME = NofspSerializationConstants.tlvFrame;

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

        byte[] typeField = TlvReader.getTypeField(bytes, TLV_FRAME);
        byte[] valueField = TlvReader.getValueField(bytes, TLV_FRAME);

        ByteSerializable serializable = null;

        // type field: integer
        if (Arrays.equals(typeField, NofspSerializationConstants.INTEGER_BYTES)) {
            serializable = getInteger(valueField);
        }

        // type field: list
        if (Arrays.equals(typeField, NofspSerializationConstants.LIST_BYTES)) {
            Class<? extends ByteSerializable> listElementTypeClass = getListElementClass(valueField);

            if (listElementTypeClass != null) {
                serializable = getList(valueField, listElementTypeClass);
            }
        }

        return serializable;
    }

    private static ByteSerializableInteger getInteger(byte[] bytes) {
        return new ByteSerializableInteger(ByteHandler.bytesToInt(bytes));
    }

    private static <T extends ByteSerializable> ByteSerializableList<T> getList(byte[] bytes, Class<T> typeClass) {
        ByteSerializableList<T> list = new ByteSerializableList<>();

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.tlvFrame);

        byte[] tlv = tlvReader.readNextTlv();
        while (tlv != null) {
            ByteSerializable serializable = deserialize(tlv);
            if (typeClass.isInstance(serializable)) {
                list.add(typeClass.cast(serializable));
            }

            tlv = tlvReader.readNextTlv();
        }

        return list;
    }

    private static Class<? extends ByteSerializable> getListElementClass(byte[] bytes) {
        Class<? extends ByteSerializable> typeClass = null;

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.tlvFrame);

        byte[] firstElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(firstElement);
        if (serializable != null) {
            typeClass = serializable.getClass();
        }

        return typeClass;
    }
}
