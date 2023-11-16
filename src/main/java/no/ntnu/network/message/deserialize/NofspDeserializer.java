package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.TlvFrame;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A deserializer constructing common {@code ByteSerializable} objects from arrays of bytes.
 * The deserializer implements the technique described by NOFSP.
 */
public abstract class NofspDeserializer {
    protected static final TlvFrame TLV_FRAME = NofspSerializationConstants.TLV_FRAME;

    /**
     * Deserializes a TLV of bytes into a {@code ByteSerializable} object.
     *
     * @param bytes bytes to deserialize
     * @return a {@code ByteSerializable} object
     * @throws SerializationException thrown when bytes cannot be deserialized
     */
    protected ByteSerializable deserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            throw new SerializationException("Cannot identify type field, because bytes is null.");
        }

        byte[] valueField = TlvReader.getValueField(bytes, TLV_FRAME);

        ByteSerializable serializable = null;

        if (tlvOfType(bytes, NofspSerializationConstants.INTEGER_BYTES)) {
            // type field: integer
            serializable = getInteger(valueField);

        } else if (tlvOfType(bytes, NofspSerializationConstants.STRING_BYTES)) {
            // type field: string
            serializable = getString(valueField);

        } else if (tlvOfType(bytes, NofspSerializationConstants.LIST_BYTES)) {
            // type field: list
            Class<? extends ByteSerializable> listElementTypeClass = getListElementClass(valueField);

            if (listElementTypeClass != null) {
                serializable = getList(valueField, listElementTypeClass);
            }

        } else if (tlvOfType(bytes, NofspSerializationConstants.MAP_BYTES)) {
            // type field: map
            Class<? extends ByteSerializable> mapKeyTypeClass = getMapKeyClass(valueField);
            Class<? extends ByteSerializable> mapValueTypeClass = getMapValueClass(valueField);

            serializable = getMap(valueField, mapKeyTypeClass, mapValueTypeClass);

        }

        return serializable;
    }

    /**
     * Returns whether a {@code ByteSerializableInteger} equals to an actual integer.
     *
     * @param serializableInteger the serializable integer
     * @param integer the actual integer
     * @return true if the integers are equals, false otherwise
     */
    protected static boolean integerEquals(ByteSerializableInteger serializableInteger, int integer) {
        return serializableInteger.getInteger() == integer;
    }

    /**
     * Returns whether a {@code ByteSerializableString} equals to an actual String.
     *
     * @param serializableString the serializable string
     * @param string the actual string
     * @return true if the strings equals, false otherwise
     */
    protected static boolean stringEquals(ByteSerializableString serializableString, String string) {
        return serializableString.getString().equals(string);
    }

    /**
     * Returns the message ID for a message TLV wrapped in a TLV reader.
     *
     * @param tlvReader the tlv reader holding the message tlv
     * @return the corresponding message id
     * @throws IOException thrown if an I/O exception occurs
     */
    protected static ByteSerializableInteger getMessageId(TlvReader tlvReader) throws IOException {
        byte[] messageIdTlv = tlvReader.readNextTlv();

        // checks if tlv is not of type integer
        if (!tlvOfType(messageIdTlv, NofspSerializationConstants.INTEGER_BYTES)) {
            throw new IOException("Cannot deserialize message, because message-ID TLV was not recognized: "
            + ByteHandler.bytesToString(messageIdTlv));
        }

        return getInteger(TlvReader.getValueField(messageIdTlv, TLV_FRAME));
    }

    /**
     * Returns the status code for a message TLV wrapped in a TLV reader.
     *
     * @param tlvReader the tlv reader holding the message tlv
     * @return the corresponding status code
     * @throws IOException throw nif an I/O exception occurs
     */
    protected static ByteSerializableInteger getStatusCode(TlvReader tlvReader) throws IOException {
        byte[] statusCodeTlv = tlvReader.readNextTlv();

        // checks if tlv is not of type integer
        if (!tlvOfType(statusCodeTlv, NofspSerializationConstants.INTEGER_BYTES)) {
            throw new IOException("Cannot deserialize message, because status code TLV was not recognized: " +
                    ByteHandler.bytesToString(statusCodeTlv));
        }

        return getInteger(TlvReader.getValueField(statusCodeTlv, TLV_FRAME));
    }

    /**
     * Returns the command for a request message TLV wrapped in a TLV reader.
     *
     * @param tlvReader the tlv reader holding the command tlv
     * @return the corresponding command
     * @throws IOException thrown if an I/O error occurs
     */
    protected static ByteSerializableString getRequestMessageCommand(TlvReader tlvReader) throws IOException {
        byte[] commandTlv = tlvReader.readNextTlv();

        // checks if tlv is not of type string
        if (!tlvOfType(commandTlv, NofspSerializationConstants.STRING_BYTES)) {
            throw new IOException("Cannot deserialize message, because command TLV was not recognized: "
            + ByteHandler.bytesToString(commandTlv));
        }

        return getString(TlvReader.getValueField(commandTlv, TLV_FRAME));
    }

    /**
     * Checks whether a given TLV is of a particular type.
     *
     * @param tlv tlv to check
     * @param typeBytes bytes representing a particular data type
     * @return true if tlv represents the given type, false otherwise
     */
    protected static boolean tlvOfType(byte[] tlv, byte[] typeBytes) {
        return Arrays.equals(TlvReader.getTypeField(tlv, TLV_FRAME), typeBytes);
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableString}, using UTF-8 decoding.
     *
     * @param bytes bytes to deserialize
     * @return the corresponding String
     */
    protected static ByteSerializableString getString(byte[] bytes) {
        ByteSerializableString result = null;

        String deserializedString = new String(bytes, StandardCharsets.UTF_8);
        result = new ByteSerializableString(deserializedString);

        return result;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableInteger}.
     *
     * @param bytes bytes to deserialize
     * @return the corresponding integer
     */
    protected static ByteSerializableInteger getInteger(byte[] bytes) {
        ByteSerializableInteger result = null;

        int deserializedInt = ByteHandler.bytesToInt(bytes);
        result = new ByteSerializableInteger(deserializedInt);

        return result;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableSet}, with a predefined class for its elements.
     * Only elements with this given class will be added to the resulting set.
     *
     * @param bytes bytes representing the content of the set
     * @param typeClass the class of elements in the set to accept
     * @return the set containing all valid elements
     * @param <T> class of elements in the set, which implements the {@code ByteSerializable} interface
     * @throws IOException thrown if an I/O exception occurs
     */
    protected <T extends ByteSerializable> ByteSerializableSet<T> getSet(byte[] bytes, Class<T> typeClass) throws IOException {
       ByteSerializableSet<T> set = new ByteSerializableSet<>();

       TlvReader elementReader = new TlvReader(bytes, TLV_FRAME);

       boolean readerEmpty = false;
       while (!readerEmpty) {
           byte[] tlv = elementReader.readNextTlv();

           if (tlv != null) {
               ByteSerializable serializable = deserialize(tlv);
               if (typeClass.isInstance(serializable)) {
                   set.add(typeClass.cast(serializable));
               }

           } else {
               readerEmpty = true;
           }
       }

       return set;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableList}, with a predefined class for its elements.
     * Only elements with this given class will be added to the resulting list.
     *
     * @param bytes bytes representing the content of the list
     * @param typeClass the class of elements in the list to accept
     * @return the list containing all valid elements
     * @param <T> class of elements in the list, which implements the {@code ByteSerializable} interface
     * @throws IOException thrown if an I/O exception occurs
     */
    protected <T extends ByteSerializable> ByteSerializableList<T> getList(byte[] bytes, Class<T> typeClass) throws IOException {
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

    /**
     * Identifies the corresponding class for a given serialized TLV.
     *
     * @param bytes bytes to inspect
     * @return the class of object
     * @throws IOException thrown if an I/O exception occurs
     */
    private Class<? extends ByteSerializable> getListElementClass(byte[] bytes) throws IOException {
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

    /**
     * Deserializes an array of bytes to a {@code ByteSerializableMap}, with given classes for key and value elements
     * for the entries. Only key-value pairs of these classes will be deserialized and added to the result.
     *
     * @param bytes bytes to deserialize
     * @param mapKeyTypeClass the class for key elements
     * @param mapValueTypeClass the class for value elements
     * @return the deserialized map containing all valid entries
     * @param <K> class of key elements in the entries, implementing the {@code ByteSerializable} interface
     * @param <V> class of value elements in the entries, implementing the {@code ByteSerializable} interface
     * @throws IOException thrown if an I/O exception occurs
     */
    private <K extends ByteSerializable, V extends ByteSerializable> ByteSerializableMap<K, V> getMap(byte[] bytes, Class<K> mapKeyTypeClass, Class<V> mapValueTypeClass) throws IOException {
        ByteSerializableMap<K, V> map = new ByteSerializableMap<>();

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            K key = null;
            V value = null;

            byte[] entry = getNextMapEntry(tlvReader);
            if (entry != null) {
                TlvReader entryReader = new TlvReader(entry, TLV_FRAME);

                key = deserializeOfTypeClass(mapKeyTypeClass, entryReader.readNextTlv());
                value = deserializeOfTypeClass(mapValueTypeClass, entryReader.readNextTlv());

                if (key != null) {
                    map.put(key, value);
                } else {
                    throw new SerializationException("Cannot create map key with null value.");
                }

        } else {
            readerEmpty = true;
        }
        }

        return map;
    }

    /**
     * Returns the next entry for a serialized map.
     *
     * @param tlvReader the TlvReader holding the map entries
     * @return the next map entry, null if no more entries exists
     * @throws IOException thrown if an I/O exception occurs
     */
    private static byte[] getNextMapEntry(TlvReader tlvReader) throws IOException {
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

    /**
     * Identifies the corresponding entry key class for a given serialized map.
     *
     * @param bytes the bytes of the TLV value field for the map
     * @return the corresponding class
     * @throws IOException thrown if an I/O exception occurs
     */
    private Class<? extends ByteSerializable> getMapKeyClass(byte[] bytes) throws IOException {
        Class<? extends ByteSerializable> keyClass = null;

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.TLV_FRAME);

        byte[] firstElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(firstElement);
        if (serializable != null) {
            keyClass = serializable.getClass();
        } else {
            throw new SerializationException("Cannot identify key-type for map: " + ByteHandler.bytesToString(bytes));
        }

        return keyClass;
    }

    /**
     * Identifies the corresponding entry value class for a given serialized map.
     *
     * @param bytes the bytes of the TLV value field for the map
     * @return the corresponding class
     * @throws IOException thrown if an I/O exception is thrown
     */
    private Class<? extends ByteSerializable> getMapValueClass(byte[] bytes) throws IOException {
        Class<? extends ByteSerializable> valueClass = null;

        TlvReader tlvReader = new TlvReader(bytes, NofspSerializationConstants.TLV_FRAME);

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

    /**
     * Deserializes an array of bytes to an object of a predefined class.
     * This is a useful mechanism for safe casting deserialized objects to any given class.
     *
     * @param elementTypeClass the predefined class of object
     * @param bytes bytes to deserialize
     * @return deserialized object of predefined class, null if object is not of that class
     * @param <E> class of object, implementing the {@code ByteSerializable} interface
     * @throws IOException thrown if an I/O exception occurs
     */
    private <E extends ByteSerializable> E deserializeOfTypeClass(Class<E> elementTypeClass, byte[] bytes) throws IOException {
        E element = null;

        ByteSerializable serializable = deserialize(bytes);
        if (elementTypeClass.isInstance(serializable)) {
            element = elementTypeClass.cast(serializable);
        }

        return element;
    }

    public TlvFrame getTlvFrame() {
        return TLV_FRAME;
    }
}
