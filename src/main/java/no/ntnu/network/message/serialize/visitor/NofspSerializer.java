package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.common.byteserializable.ByteSerializableList;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.ByteHandler;

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
        byte[] lengthField = null;

        byte[] valueBytes = ByteHandler.intToBytes(integer.getInteger());
        lengthField = getLengthField(valueBytes.length);

        SimpleByteBuffer tlv = new SimpleByteBuffer();
        tlv.addBytes(NofspSerializationConstants.INTEGER_BYTES, lengthField, valueBytes);

        return tlv.toArray();
    }

    @Override
    public <T extends ByteSerializable> byte[] visitList(ByteSerializableList<T> list) throws SerializationException {
        if (list.isEmpty()) {
            throw new SerializationException("Cannot serialize list, because list is empty.");
        }

        byte[] lengthField = null;
        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();

        list.forEach(
                item -> valueBuffer.addBytes(item.accept(this))
        );

        byte[] valueField = valueBuffer.toArray();
        lengthField = getLengthField(valueField.length);

        SimpleByteBuffer tlv = new SimpleByteBuffer();
        tlv.addBytes(NofspSerializationConstants.LIST_BYTES, lengthField, valueField);

        return tlv.toArray();
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
