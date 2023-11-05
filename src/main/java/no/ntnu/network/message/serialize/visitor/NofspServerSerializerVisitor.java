package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.common.byteserializable.ByteSerializableList;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.NofspCommonSerializer;

/**
 * A serializer for the central server, implementing the serialization described by NOFSP.
 * In short terms, the serializer serializes data into TLV structures recursively.
 */
public class NofspServerSerializerVisitor implements ByteSerializerVisitor {
    @Override
    public byte[] serialize(ByteSerializable serializable) throws SerializationException {
        return serializable.accept(this);
    }

    @Override
    public byte[] visitInteger(ByteSerializableInteger integer) {
        return NofspCommonSerializer.serializeInteger(integer);
    }

    @Override
    public byte[] visitList(ByteSerializableList list) throws SerializationException {
        return new byte[0];
    }
}
