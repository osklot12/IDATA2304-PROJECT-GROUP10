package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.TlvFrame;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.nio.charset.StandardCharsets;
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

        // type field: string
        if (Arrays.equals(typeField, NofspSerializationConstants.STRING_BYTES)) {
            serializable = getString(valueField);
        }

        // type field: list
        if (Arrays.equals(typeField, NofspSerializationConstants.LIST_BYTES)) {
            Class<? extends ByteSerializable> listElementTypeClass = getListElementClass(valueField);

            if (listElementTypeClass != null) {
                serializable = getList(valueField, listElementTypeClass);
            }
        }

        // type field: map
        if (Arrays.equals(typeField, NofspSerializationConstants.MAP_BYTES)) {
            Class<? extends ByteSerializable> mapKeyTypeClass = getMapKeyClass(valueField);
            Class<? extends ByteSerializable> mapValueTypeClass = getMapValueClass(valueField);

            serializable = getMap(valueField, mapKeyTypeClass, mapValueTypeClass);
        }

        return serializable;
    }

    private static ByteSerializableString getString(byte[] bytes) {
        ByteSerializableString result = null;

        String deserializedString = new String(bytes, StandardCharsets.UTF_8);
        result = new ByteSerializableString(deserializedString);

        return result;
    }

    private static ByteSerializableInteger getInteger(byte[] bytes) {
        ByteSerializableInteger result = null;

        int deserializedInt = ByteHandler.bytesToInt(bytes);
        result = new ByteSerializableInteger(deserializedInt);

        return result;
    }

    private static <T extends ByteSerializable> ByteSerializableList<T> getList(byte[] bytes, Class<T> typeClass) {
        ByteSerializableList<T> list = new ByteSerializableList<>();

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            byte[] tlv = tlvReader.readNextTlv();

            if (tlv != null) {
                ByteSerializable serializable = deserialize(tlv);
                if (typeClass.isInstance(serializable)) {
                    list.add(typeClass.cast(serializable));
                }

            } else {
                readerEmpty = true;
            }

        }

        return list;
    }

    private static Class<? extends ByteSerializable> getListElementClass(byte[] bytes) throws SerializationException {
        Class<? extends ByteSerializable> typeClass = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        byte[] firstElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(firstElement);
        if (serializable != null) {
            typeClass = serializable.getClass();
        } else {
            throw new SerializationException("Cannot identify element-type for list: " + ByteHandler.bytesToString(bytes));
        }

        return typeClass;
    }

    private static <K extends ByteSerializable, V extends ByteSerializable> ByteSerializableMap<K, V> getMap(byte[] bytes, Class<K> mapKeyTypeClass, Class<V> mapValueTypeClass) throws SerializationException {
        ByteSerializableMap<K, V> map = new ByteSerializableMap<>();

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            K key = null;
            V value = null;

            byte[] entry = getMapEntry(tlvReader);
            if (entry != null) {
                TlvReader entryReader = new TlvReader(entry, TLV_FRAME);

                key = deserializeEntryKey(mapKeyTypeClass, entryReader);
                value = deserializeEntryValue(mapValueTypeClass, entryReader);
                map.put(key, value);
            } else {
                readerEmpty = true;
            }
        }

        return map;
    }

    private static <V extends ByteSerializable> V deserializeEntryValue(Class<V> mapValueTypeClass, TlvReader entryReader) {
        V value = null;

        byte[] valueBytes = entryReader.readNextTlv();
        ByteSerializable valueSerializable = deserialize(valueBytes);
        if (mapValueTypeClass.isInstance(valueSerializable)) {
            value = mapValueTypeClass.cast(valueSerializable);
        } else {
            throw new SerializationException("Invalid map value type: " + ByteHandler.bytesToString(valueBytes));
        }

        return value;
    }

    private static <K extends ByteSerializable> K deserializeEntryKey(Class<K> mapKeyTypeClass, TlvReader entryReader) {
        K key = null;

        byte[] keyBytes = entryReader.readNextTlv();
        ByteSerializable keySerializable = deserialize(keyBytes);
        if (mapKeyTypeClass.isInstance(keySerializable)) {
            key = mapKeyTypeClass.cast(keySerializable);
        } else {
            throw new SerializationException("Invalid map key type: " + ByteHandler.bytesToString(keyBytes));
        }

        return key;
    }

    private static byte[] getMapEntry(TlvReader tlvReader) {
        byte[] entry = null;

        // read key and value
        byte[] keyTlv = tlvReader.readNextTlv();
        byte[] valueTlv = tlvReader.readNextTlv();

        // create entry
        if (keyTlv != null && valueTlv != null) {
            SimpleByteBuffer entryBuffer = new SimpleByteBuffer();
            entryBuffer.addBytes(keyTlv, valueTlv);
            entry = entryBuffer.toArray();
        }

        return entry;
    }

    private static Class<? extends ByteSerializable> getMapValueClass(byte[] bytes) {
        Class<? extends ByteSerializable> valueClass = null;

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.tlvFrame);

        tlvReader.readNextTlv();
        byte[] secondElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(secondElement);
        if (serializable != null) {
            valueClass = serializable.getClass();
        } else {
            throw new SerializationException("Cannot identify value-type for map: " + ByteHandler.bytesToString(bytes));
        }

        return valueClass;
    }

    private static Class<? extends ByteSerializable> getMapKeyClass(byte[] bytes) throws SerializationException {
        Class<? extends ByteSerializable> keyClass = null;

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.tlvFrame);

        byte[] firstElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(firstElement);
        if (serializable != null) {
            keyClass = serializable.getClass();
        } else {
            throw new SerializationException("Cannot identify key-type for map: " + ByteHandler.bytesToString(bytes));
        }

        return keyClass;
    }
}
