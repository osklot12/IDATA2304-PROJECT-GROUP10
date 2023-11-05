package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.common.byteserializable.ByteSerializableList;
import no.ntnu.network.message.serialize.composite.ByteSerializable;

/**
 * A serializer acting as the visitor in the Visitor design pattern.
 * A class implementing the interface extracts the serialization methods from the objects on which they operate.
 * <p/>
 * The class works with objects implementing the {@code ByteSerializable} interface, which is used as a component
 * in the Composite design pattern. Together, they create a powerful mechanism for serializing composite objects
 * with any strategy desirable.
 */
public interface ByteSerializerVisitor {
    /**
     * Serializes a {@code ByteSerializable} object.
     *
     * @param serializable the object to serialize
     * @return serialized bytes
     * @throws SerializationException thrown if serialization fails
     */
    byte[] serialize(ByteSerializable serializable) throws SerializationException;

    /**
     * Visits a {@code ByteSerializableInteger}.
     *
     * @param integer byte serializable integer to visit
     * @return the serialized integer
     * @throws SerializationException thrown when integer cannot be serialized
     */
    byte[] visitInteger(ByteSerializableInteger integer) throws SerializationException;

    /**
     * Visits a {@code ByteSerializableList}.
     *
     * @param list byte serializable list to visit
     * @return the serialized list
     * @throws SerializationException thrown when list cannot be serialized
     */
    byte[] visitList(ByteSerializableList list) throws SerializationException;
}
