package no.ntnu.network.message.deserialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.request.Request;
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
 * A deserializer constructing {@code ByteSerializable} objects from arrays of bytes.
 * The deserializer implements the technique described by NOFSP.
 */
public class NofspDeserializer {
    private static final TlvFrame TLV_FRAME = NofspSerializationConstants.TLV_FRAME;

    private NofspDeserializer() {

    }

    /**
     * Deserializes an array of bytes.
     *
     * @param bytes bytes to deserialize
     * @return a {@code ByteSerializable} object
     * @throws SerializationException thrown when bytes cannot be deserialized
     */
    public static ByteSerializable deserialize(byte[] bytes) throws IOException {
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

        // type field: request message
        if (Arrays.equals(typeField, NofspSerializationConstants.REQUEST_BYTES)) {
            serializable = getRequestMessage(valueField);
        }

        return serializable;
    }

    /**
     * Deserializes an array of bytes into a {@code Request}.
     *
     * @param bytes bytes to deserialize
     * @return the request object
     * @throws IOException thrown if an I/O exception occurs
     */
    private static Request getRequestMessage(byte[] bytes) throws IOException {
        Request request = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        // first TLV holds the command
        byte[] commandTlv = tlvReader.readNextTlv();
        ByteSerializable firstSerializable = deserialize(commandTlv);

        if (firstSerializable instanceof ByteSerializableString string) {
            String command = string.getString();

            // second TLV holds the data
            byte[] data = tlvReader.readNextTlv();

            // REGCP: register control panel request
            if (command.equals(NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND)) {
                request = getRegisterControlPanelRequest(data);
            }


        } else {
            throw new SerializationException("Cannot recognize request command: " + ByteHandler.bytesToString(commandTlv));
        }

        return request;
    }

    /**
     * Deserializes an array of bytes into a {@code RegisterControlPanelRequest}.
     *
     * @param bytes bytes to deserialize
     * @return the request object
     * @throws IOException thrown if an I/O exception occurs
     */
    private static RegisterControlPanelRequest getRegisterControlPanelRequest(byte[] bytes) throws IOException {
        RegisterControlPanelRequest request = null;

        ByteSerializable serializable = deserialize(bytes);
        if (serializable instanceof ByteSerializableList<?> serializableList) {
            Set<DeviceClass> compatibilityList = getCompatibilityList(serializableList);

            request = new RegisterControlPanelRequest(compatibilityList);
        } else {
            throw new SerializationException("Cannot create RegisterControlPanelRequest, because compatibility list is not recognized.");
        }

        return request;
    }

    /**
     * Converts a {@code ByteSerializableList} into a compatibility list.
     * A compatibility list consists of {@code ByteSerializableString} elements representing {@code DeviceClass}
     * constants. Only elements matching the constant names will be valid.
     *
     * @param serializableList the serializable list to convert
     * @return the compatibility list as a set of {@code DeviceClass} constants
     */
    private static Set<DeviceClass> getCompatibilityList(ByteSerializableList<?> serializableList) {
        Set<DeviceClass> compatibilityList = new HashSet<>();

        serializableList.forEach(
                item -> {
                    if (item instanceof ByteSerializableString string) {
                        DeviceClass deviceClass = DeviceClass.getDeviceClass(string.getString());

                        if (deviceClass != null) {
                            compatibilityList.add(deviceClass);
                        }
                    }
                }
        );

        return compatibilityList;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableString}, using UTF-8 decoding.
     *
     * @param bytes bytes to deserialize
     * @return the corresponding String
     */
    private static ByteSerializableString getString(byte[] bytes) {
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
    private static ByteSerializableInteger getInteger(byte[] bytes) {
        ByteSerializableInteger result = null;

        int deserializedInt = ByteHandler.bytesToInt(bytes);
        result = new ByteSerializableInteger(deserializedInt);

        return result;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableList}, with a predefined class for its elements.
     * Only elements with this given class will be added to the resulting list.
     *
     * @param bytes bytes to deserialize
     * @param typeClass the class of elements in the list to accept
     * @return the list containing all valid elements
     * @param <T> class of elements in the list, which implements the {@code ByteSerializable} interface
     * @throws IOException thrown if an I/O exception occurs
     */
    private static <T extends ByteSerializable> ByteSerializableList<T> getList(byte[] bytes, Class<T> typeClass) throws IOException {
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
    private static Class<? extends ByteSerializable> getListElementClass(byte[] bytes) throws IOException {
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
    private static <K extends ByteSerializable, V extends ByteSerializable> ByteSerializableMap<K, V> getMap(byte[] bytes, Class<K> mapKeyTypeClass, Class<V> mapValueTypeClass) throws IOException {
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
    private static Class<? extends ByteSerializable> getMapKeyClass(byte[] bytes) throws IOException {
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
    private static Class<? extends ByteSerializable> getMapValueClass(byte[] bytes) throws IOException {
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
    private static <E extends ByteSerializable> E deserializeOfTypeClass(Class<E> elementTypeClass, byte[] bytes) throws IOException {
        E element = null;

        ByteSerializable serializable = deserialize(bytes);
        if (elementTypeClass.isInstance(serializable)) {
            element = elementTypeClass.cast(serializable);
        }

        return element;
    }
}
