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

/**
 * A serializer that handles serialization of common data using the serialization technique described by NOFSP.
 */
public class NofspSerializer implements ByteSerializerVisitor {
    /**
     * Creates a new NofspSerializer.
     */
    public NofspSerializer() {

    }

    @Override
    public byte[] serialize(ByteSerializable serializable) throws SerializationException {
        return serializable.accept(this);
    }

    @Override
    public byte[] visitInteger(ByteSerializableInteger integer) throws SerializationException {
        byte[] tlv = null;

        byte[] typeField = NofspSerializationConstants.INTEGER_BYTES;
        byte[] lengthField = null;
        byte[] valueBytes = ByteHandler.intToBytes(integer.getInteger());

        lengthField = getLengthField(valueBytes.length);
        tlv = createTlv(typeField, lengthField, valueBytes);

        return tlv;
    }

    @Override
    public byte[] visitString(ByteSerializableString string) throws SerializationException {
        byte[] tlv = null;

        byte[] typeField = NofspSerializationConstants.STRING_BYTES;
        byte[] lengthField = null;
        byte[] valueField = string.getString().getBytes(StandardCharsets.UTF_8);

        lengthField = getLengthField(valueField.length);
        tlv = createTlv(typeField, lengthField, valueField);

        return tlv;
    }

    @Override
    public <T extends ByteSerializable> byte[] visitList(ByteSerializableList<T> list) throws SerializationException {
        if (list.isEmpty()) {
            throw new SerializationException("Cannot serialize list, because list is empty.");
        }

        byte[] tlv = null;

        byte[] typeField = NofspSerializationConstants.LIST_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        list.forEach(
                item -> valueBuffer.addBytes(item.accept(this))
        );

        valueField = valueBuffer.toArray();
        lengthField = getLengthField(valueField.length);

        tlv = createTlv(typeField, lengthField, valueField);

        return tlv;
    }

    @Override
    public <K extends ByteSerializable, V extends ByteSerializable> byte[] visitMap(ByteSerializableMap<K, V> map) throws SerializationException {
        if (map.isEmpty()) {
            throw new SerializationException("Cannot serialize map, because map is empty");
        }

        byte[] tlv = null;

        byte[] typeField = NofspSerializationConstants.MAP_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        map.forEach((key, value) -> {
            valueBuffer.addBytes(key.accept(this));

            if (value != null) {
                valueBuffer.addBytes(value.accept(this));
            } else {
                valueBuffer.addBytes(createNullValueTlv());
            }
        });

        valueField = valueBuffer.toArray();
        lengthField = getLengthField(valueField.length);

        tlv = createTlv(typeField, lengthField, valueField);

        return tlv;
    }

    @Override
    public byte[] visitRegisterControlPanelRequest(RegisterControlPanelRequest request) throws SerializationException {
        byte[] tlv = null;

        byte[] commandTlv = serialize(new ByteSerializableString(request.getCommand()));
        byte[] dataTlv = request.getSerializableCompatibilityList().accept(this);

        tlv = packInRequestFrame(commandTlv, dataTlv);

        return tlv;
    }

    private byte[] packInRequestFrame(byte[] commandTlv, byte[] dataTlv) {
        byte[] tlv = null;

        byte[] typeField = NofspSerializationConstants.REQUEST_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        valueBuffer.addBytes(commandTlv, dataTlv);
        valueField = valueBuffer.toArray();
        lengthField = getLengthField(valueField.length);

        tlv = createTlv(typeField, lengthField, valueField);

        return tlv;
    }

    private byte[] getProtocolVersionTlv() {
        byte[] versionTlv = null;

        String versionString = NofspSerializationConstants.VERSION;
        ByteSerializableString serializableString = new ByteSerializableString(versionString);
        versionTlv = serialize(serializableString);

        return versionTlv;
    }

    private static byte[] createTlv(byte[] typeField, byte[] lengthField, byte[] valueBytes) {
        byte[] tlv = null;

        SimpleByteBuffer tlvBuffer = new SimpleByteBuffer();
        tlvBuffer.addBytes(typeField, lengthField, valueBytes);
        tlv = tlvBuffer.toArray();

        return tlv;
    }

    private static byte[] createNullValueTlv() {
        byte[] tlv = null;

        SimpleByteBuffer tlvBuffer = new SimpleByteBuffer();
        tlvBuffer.addBytes(NofspSerializationConstants.NULL_BYTES, getLengthField(0), new byte[] {});
        tlv = tlvBuffer.toArray();

        return tlv;
    }

    private static byte[] getLengthField(int length) throws SerializationException {
        byte[] lengthField = null;

        try {
            lengthField = ByteHandler.addLeadingPadding(ByteHandler.intToBytes(length), NofspSerializationConstants.tlvFrame.lengthFieldLength());
        } catch (IllegalArgumentException e) {
            throw new SerializationException(e.getMessage());
        }

        return lengthField;
    }
}
