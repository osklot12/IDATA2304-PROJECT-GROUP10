package no.ntnu.network.message.deserialize.component;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.*;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A deserializer for deserialization of common data types used in NOFSP.
 * These data types are necessary for constructing all the messages described by the protocol and includes:
 * <ul>
 *     <li>
 *         Integer
 *     </li>
 *     <li>
 *         Double
 *     </li>
 *     <li>
 *         String
 *     </li>
 *     <li>
 *         Set
 *     </li>
 *     <li>
 *         List
 *     </li>
 *     <li>
 *         Map
 *     </li>
 * </ul>
 * <p/>
 * The class is abstract and serves as a base class for any message deserializer in the application.
 */
public abstract class NofspDeserializer {
    /**
     * A functional interface defining the method used for deserialization of data types in NOFSP.
     */
    @FunctionalInterface
    interface DataTypeDeserializer {
        ByteSerializable deserialize(Tlv tlv) throws IOException;
    }

    private final Map<String, DataTypeDeserializer> deserializerMap = new HashMap<>();

    /**
     * Creates a new NofspDeserializer.
     */
    protected NofspDeserializer() {
        initializeDeserializerMap();
    }

    /**
     * Initializes all the entries for the deserializer map, which is used to map TLV type fields to
     * deserialization methods.
     */
    private void initializeDeserializerMap() {
        // type: Integer
        deserializerMap.put(ByteHandler.bytesToString(NofspSerializationConstants.INTEGER_BYTES), this::getInteger);

        // type: String
        deserializerMap.put(ByteHandler.bytesToString(NofspSerializationConstants.STRING_BYTES), this::getString);

        // type: Set
        deserializerMap.put(ByteHandler.bytesToString(NofspSerializationConstants.SET_BYTES), this::getSet);

        // type: List
        deserializerMap.put(ByteHandler.bytesToString(NofspSerializationConstants.LIST_BYTES), this::getList);

        // type: Map
        deserializerMap.put(ByteHandler.bytesToString(NofspSerializationConstants.MAP_BYTES), this::getMap);
    }

    /**
     * Deserializes a TLV into a {@code ByteSerializable} object.
     *
     * @param tlv the TLV to deserialize
     * @return the corresponding deserialized object, null if TLV was not recognized
     * @throws IOException thrown if an I/O exception occurs
     */
    protected ByteSerializable deserialize(Tlv tlv) throws IOException {
        if (tlv == null) {
            throw new IllegalArgumentException("Cannot deserialize TLV, because TLV is null");
        }

        ByteSerializable result = null;

        String bytesKey = ByteHandler.bytesToString(tlv.typeField());
        DataTypeDeserializer deserializer = deserializerMap.get(bytesKey);
        if (deserializer != null) {
            result = deserializer.deserialize(tlv);
        }

        return result;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableInteger}.
     *
     * @param tlv the integer tlv
     * @return the corresponding integer
     */
    protected ByteSerializableInteger getInteger(Tlv tlv) {
        ByteSerializableInteger result = null;

        int deserializedInt = ByteHandler.bytesToInt(tlv.valueField());
        result = new ByteSerializableInteger(deserializedInt);

        return result;
    }

    /**
     * Converts an integer Tlv as a regular int.
     *
     * @param intTlv the integer tlv to deserialize
     * @return a regular int
     */
    protected int getRegularInt(Tlv intTlv) {
        if (intTlv == null) {
            throw new IllegalArgumentException("Cannot deserialize TLV, because intTlv is null.");
        }

        return getInteger(intTlv).getInteger();
    }

    /**
     * Deserializes a TLV of bytes into a {@code ByteSerializableDouble}.
     *
     * @param doubleTlv the double tlv
     * @return the corresponding double
     */
    protected ByteSerializableDouble getDouble(Tlv doubleTlv) {
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.put(doubleTlv.valueField());
        buffer.flip();

        return new ByteSerializableDouble(buffer.getDouble());
    }

    /**
     * Converts a double Tlv to a regular double.
     *
     * @param doubleTlv the double tlv
     * @return a regular double
     */
    protected double getRegularDouble(Tlv doubleTlv) {
        if (doubleTlv == null) {
            throw new IllegalArgumentException("Cannot deserialize TLV, because doubleTlv is null.");
        }

        return getDouble(doubleTlv).getDouble();
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableString}, using UTF-8 decoding.
     *
     * @param tlv the string tlv
     * @return the corresponding String
     */
    protected ByteSerializableString getString(Tlv tlv) {
        ByteSerializableString result = null;

        String deserializedString = new String(tlv.valueField(), StandardCharsets.UTF_8);
        result = new ByteSerializableString(deserializedString);

        return result;
    }

    /**
     * Returns a String Tlv as a regular string.
     *
     * @param stringTlv the string tlv to deserialize
     * @return a regular string
     */
    protected String getRegularString(Tlv stringTlv) {
        if (stringTlv == null) {
            throw new IllegalArgumentException("Cannot deserialize TLV, because descriptionTlv is null.");
        }

        return getString(stringTlv).toString();
    }

    /**
     * Identifies the element type for a serialized set and returns it accordingly.
     * The element type for the set is identified by the first element in the set, and only elements of this type
     * will be added to the result.
     *
     * @param tlv the set tlv
     * @return the reconstructed set, null on error
     * @throws IOException thrown if an I/O exception is thrown
     */
    private ByteSerializable getSet(Tlv tlv) throws IOException {
        ByteSerializableSet<?> set = null;

        try {
            Class<? extends ByteSerializable> setElementTypeClass = getTlvCollectionTypeClass(tlv);
            set = getSetOfType(tlv, setElementTypeClass);
        } catch (IOException e) {
            throw new IOException("Cannot deserialize set: " + e.getMessage());
        }

        return set;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableSet}, with a predefined class for its elements.
     * Only elements with this given class will be added to the resulting set.
     *
     * @param tlv       the list tlv
     * @param typeClass the class of elements in the set to accept
     * @param <T>       class of elements in the set, which implements the {@code ByteSerializable} interface
     * @return the set containing all valid elements
     * @throws IOException thrown if an I/O exception occurs
     */
    protected <T extends ByteSerializable> ByteSerializableSet<T> getSetOfType(Tlv tlv, Class<T> typeClass) throws IOException {
        ByteSerializableSet<T> set = new ByteSerializableSet<>();

        // assuming the list elements use the same tlv frame as the actual list tlv
        TlvFrame tlvFrame = tlv.getFrame();
        TlvReader elementReader = new TlvReader(tlv.valueField(), tlvFrame);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            Tlv elementTlv = elementReader.readNextTlv();

            if (elementTlv != null) {
                ByteSerializable serializable = deserialize(elementTlv);
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
     * Identifies the element type for a serialized list and returns it accordingly.
     * The element type for the list is identified by the first element in the list, and only elements of this type
     * will be added to the final result.
     *
     * @param tlv the list tlv
     * @return the reconstructed list, null on error
     * @throws IOException thrown if an I/O exception is thrown
     */
    private ByteSerializableList<?> getList(Tlv tlv) throws IOException {
        ByteSerializableList<?> list = null;

        try {
            Class<? extends ByteSerializable> listElementTypeClass = getTlvCollectionTypeClass(tlv);
            if (listElementTypeClass != null) {
                list = getListOfType(tlv, listElementTypeClass);
            }
        } catch (IOException e) {
            throw new IOException("Cannot deserialize list: " + e.getMessage());
        }

        return list;
    }

    /**
     * Deserializes an array of bytes into a {@code ByteSerializableList}, with a predefined class for its elements.
     * Only elements with this given class will be added to the resulting list.
     *
     * @param tlv       the list tlv
     * @param typeClass the class of elements in the list to accept
     * @param <T>       class of elements in the list, which implements the {@code ByteSerializable} interface
     * @return the list containing all valid elements
     * @throws IOException thrown if an I/O exception occurs
     */
    protected <T extends ByteSerializable> ByteSerializableList<T> getListOfType(Tlv tlv, Class<T> typeClass) throws IOException {
        ByteSerializableList<T> list = new ByteSerializableList<>();

        // assuming that the list elements use the same tlv frame as the actual list tlv
        TlvFrame tlvFrame = tlv.getFrame();
        TlvReader tlvReader = new TlvReader(tlv.valueField(), tlvFrame);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            Tlv elementTlv = tlvReader.readNextTlv();

            if (elementTlv != null) {
                ByteSerializable serializable = deserialize(elementTlv);
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
     * Identifies the key-value pair type for a serialized map and returns it accordingly.
     * The key-value pair is identified by the first entry in the map, and only entries of these types will
     * be added to the final result.
     *
     * @param tlv the map tlv
     * @return the reconstructed map, null on error
     * @throws IOException thrown if an I/O exception occurs
     */
    private ByteSerializableMap<?, ?> getMap(Tlv tlv) throws IOException {
        ByteSerializableMap<?, ?> map = null;

        try {
            Class<? extends ByteSerializable> mapKeyTypeClass = getTlvCollectionTypeClass(tlv);
            Class<? extends ByteSerializable> mapValueTypeClass = getTlvCollectionTypeClass(tlv);
            map = getMapOfType(tlv, mapKeyTypeClass, mapValueTypeClass);
        } catch (IOException e) {
            throw new IOException("Cannot deserialize map: " + e.getMessage());
        }

        return map;
    }

    /**
     * Deserializes a TLV of bytes to a {@code ByteSerializableMap}, with given classes for key and value elements
     * for the entries. Only key-value pairs of these classes will be deserialized and added to the result.
     *
     * @param tlv               the map tlv
     * @param mapKeyTypeClass   the class for key elements
     * @param mapValueTypeClass the class for value elements
     * @param <K>               class of key elements in the entries, implementing the {@code ByteSerializable} interface
     * @param <V>               class of value elements in the entries, implementing the {@code ByteSerializable} interface
     * @return the deserialized map containing all valid entries
     * @throws IOException thrown if an I/O exception occurs
     */
    protected <K extends ByteSerializable, V extends ByteSerializable> ByteSerializableMap<K, V> getMapOfType(Tlv tlv, Class<K> mapKeyTypeClass, Class<V> mapValueTypeClass) throws IOException {
        ByteSerializableMap<K, V> map = new ByteSerializableMap<>();

        // assuming that the map elements use the same tlv frame as the map tlv
        TlvFrame tlvFrame = tlv.getFrame();
        TlvReader tlvReader = new TlvReader(tlv.valueField(), tlvFrame);

        boolean readerEmpty = false;
        while (!readerEmpty) {
            K key = null;
            V value = null;

            // reads the next map entry
            byte[] entry = getNextMapEntry(tlvReader);
            if (entry != null) {
                // reads the key and value from entry
                TlvReader entryReader = new TlvReader(entry, tlvFrame);
                key = deserializeOfTypeClass(mapKeyTypeClass, entryReader.readNextTlv());
                value = deserializeOfTypeClass(mapValueTypeClass, entryReader.readNextTlv());

                // puts the entry in the map if key is not null
                if (key != null) {
                    map.put(key, value);
                } else {
                    throw new IOException("Cannot create map key with null value.");
                }

            } else {
                readerEmpty = true;
            }
        }

        return map;
    }

    /**
     * Returns whether a {@code ByteSerializableInteger} equals to an actual integer.
     *
     * @param serializableInteger the serializable integer
     * @param integer             the actual integer
     * @return true if the integers are equals, false otherwise
     */
    protected static boolean integerEquals(ByteSerializableInteger serializableInteger, int integer) {
        return serializableInteger.getInteger() == integer;
    }

    /**
     * Returns whether a {@code ByteSerializableString} equals to an actual String.
     *
     * @param serializableString the serializable string
     * @param string             the actual string
     * @return true if the strings equals, false otherwise
     */
    protected static boolean stringEquals(ByteSerializableString serializableString, String string) {
        return serializableString.getString().equals(string);
    }

    /**
     * Identifies the corresponding class for a collection of TLVs.
     * The resulting class is based on the first TLV in the collection of TLVs.
     *
     * @param tlv the collection tlv
     * @return the concrete {@code ByteSerializable} class for the TLV
     * @throws IOException thrown if an I/O exception occurs
     */
    private Class<? extends ByteSerializable> getTlvCollectionTypeClass(Tlv tlv) throws IOException {
        Class<? extends ByteSerializable> typeClass = null;

        // assuming that the collection elements use the same tlv frame as the collection tlv
        TlvFrame tlvFrame = tlv.getFrame();
        TlvReader tlvReader = new TlvReader(tlv.valueField(), tlvFrame);

        Tlv firstElement = tlvReader.readNextTlv();
        ByteSerializable serializable = deserialize(firstElement);
        if (serializable != null) {
            typeClass = serializable.getClass();
        } else {
            throw new SerializationException("Cannot identify class for TLV: " + tlv.toString());
        }

        return typeClass;
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
        Tlv keyTlv = tlvReader.readNextTlv();
        Tlv valueTlv = tlvReader.readNextTlv();

        // create entry
        if (keyTlv != null && valueTlv != null) {
            SimpleByteBuffer entryBuffer = new SimpleByteBuffer();
            entryBuffer.addTlvs(keyTlv, valueTlv);
            entry = entryBuffer.toArray();
        }

        return entry;
    }

    /**
     * Deserializes a TLV of bytes to an object of a predefined class.
     * This is a useful mechanism for safe casting deserialized objects to any given class.
     *
     * @param elementTypeClass the predefined class of object
     * @param tlv              tlv to deserialize
     * @param <E>              class of object, implementing the {@code ByteSerializable} interface
     * @return deserialized object of predefined class, null if object is not of that class
     * @throws IOException thrown if an I/O exception occurs
     */
    private <E extends ByteSerializable> E deserializeOfTypeClass(Class<E> elementTypeClass, Tlv tlv) throws IOException {
        E element = null;

        ByteSerializable serializable = deserialize(tlv);
        if (elementTypeClass.isInstance(serializable)) {
            element = elementTypeClass.cast(serializable);
        }

        return element;
    }
}
