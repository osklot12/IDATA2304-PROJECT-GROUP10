package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.serialize.composite.ByteSerializable;

/**
 * A serializer for common data, acting as the visitor in the Visitor design pattern.
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
     * Serializes a {@code ByteSerializableInteger} object.
     *
     * @param integer integer to serialize
     * @return the serialized integer
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitInteger(ByteSerializableInteger integer) throws SerializationException;

    /**
     * Serializes a {@code ByteSerializableString} object.
     *
     * @param string string to serialize
     * @return the serialized string
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitString(ByteSerializableString string) throws SerializationException;

    /**
     * Serializes a {@code ByteSerializableList} object.
     *
     * @param list list to serialize
     * @return the serialized list
     * @throws SerializationException thrown if serialization fails
     */
    <T extends ByteSerializable> byte[] visitList(ByteSerializableList<T> list) throws SerializationException;
}
