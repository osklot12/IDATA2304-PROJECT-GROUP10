package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.ByteHandler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A serializer that handles serialization of {@code ByteSerializable} objects using the serialization technique
 * described by NOFSP.
 */
public class NofspSerializer implements ByteSerializerVisitor {
    private static NofspSerializer instance;

    /**
     * Creates a new NofspSerializer.
     */
    private NofspSerializer() {

    }

    /**
     * Returns an instance of the class using the singleton pattern.
     *
     * @return instance of the serializer
     */
    public static NofspSerializer getInstance() {
        if (instance == null) {
            instance = new NofspSerializer();
        }

        return instance;
    }

    @Override
    public byte[] serialize(ByteSerializable serializable) throws SerializationException {
        return serializable.accept(this);
    }

    @Override
    public byte[] visitInteger(ByteSerializableInteger integer) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.INTEGER_BYTES;
        byte[] lengthField = null;
        byte[] valueBytes = ByteHandler.intToBytes(integer.getInteger());

        lengthField = createLengthField(valueBytes.length);

        return createTlv(typeField, lengthField, valueBytes);
    }

    @Override
    public byte[] visitString(ByteSerializableString string) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.STRING_BYTES;
        byte[] lengthField = null;
        byte[] valueField = string.getString().getBytes(StandardCharsets.UTF_8);

        lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    @Override
    public <T extends ByteSerializable> byte[] visitList(ByteSerializableList<T> list) throws SerializationException {
        if (list.isEmpty()) {
            throw new SerializationException("Cannot serialize list, because list is empty.");
        }

        byte[] typeField = NofspSerializationConstants.LIST_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (ByteSerializable item : list) {
            valueBuffer.addBytes(item.accept(this));
        }

        valueField = valueBuffer.toArray();
        lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    @Override
    public <K extends ByteSerializable, V extends ByteSerializable> byte[] visitMap(ByteSerializableMap<K, V> map) throws SerializationException {
        if (map.isEmpty()) {
            throw new SerializationException("Cannot serialize map, because map is empty");
        }

        byte[] typeField = NofspSerializationConstants.MAP_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();

            valueBuffer.addBytes(key.accept(this));
            if (value != null) {
                valueBuffer.addBytes(value.accept(this));
            } else {
                valueBuffer.addBytes(createNullValueTlv());
            }
        }

        valueField = valueBuffer.toArray();
        lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    @Override
    public byte[] visitRegisterControlPanelRequest(RegisterControlPanelRequest request) throws SerializationException {
        byte[] commandTlv = serialize(new ByteSerializableString(request.getCommand()));
        byte[] dataTlv = request.getSerializableCompatibilityList().accept(this);

        return packInRequestFrame(commandTlv, dataTlv);
    }

    /**
     * Packs the content of a request message into a standard message frame for NOFSP, serializes it returns it
     * in bytes.
     *
     * @param commandTlv the TLV containing the request command
     * @param dataTlv the TLV containing the request data (command parameters)
     * @return a serialized request frame
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] packInRequestFrame(byte[] commandTlv, byte[] dataTlv) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.REQUEST_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        valueBuffer.addBytes(commandTlv, dataTlv);
        valueField = valueBuffer.toArray();
        lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    /**
     * Returns a serialized TLV containing the version of NOFSP.
     *
     * @return a serialized version tlv
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] getProtocolVersionTlv() throws SerializationException {
        String versionString = NofspSerializationConstants.VERSION;
        ByteSerializableString serializableString = new ByteSerializableString(versionString);

        return serialize(serializableString);
    }

    /**
     * Creates a TLV of three fields - type field, length field and value field.
     *
     * @param typeField field describing type of data
     * @param lengthField field indicating the length of data
     * @param valueBytes the actual data itself
     * @return serialized TLV
     */
    private static byte[] createTlv(byte[] typeField, byte[] lengthField, byte[] valueBytes) {
        return ByteHandler.combineBytes(typeField, lengthField, valueBytes);
    }

    /**
     * Creates a TLV for null values.
     *
     * @return null value TLV
     */
    private static byte[] createNullValueTlv() {
        return createTlv(NofspSerializationConstants.NULL_BYTES,
                ByteHandler.addLeadingPadding(ByteHandler.intToBytes(0), NofspSerializationConstants.TLV_FRAME.lengthFieldLength()), new byte[] {});
    }

    /**
     * Creates a length field for a TLV, for a given length.
     *
     * @param length the length
     * @return length field containing length
     * @throws SerializationException thrown if serialization fails
     */
    private static byte[] createLengthField(int length) throws SerializationException {
        byte[] lengthField = null;

        try {
            lengthField = ByteHandler.addLeadingPadding(ByteHandler.intToBytes(length), NofspSerializationConstants.TLV_FRAME.lengthFieldLength());
        } catch (IllegalArgumentException e) {
            throw new SerializationException(e.getMessage());
        }

        return lengthField;
    }
}
