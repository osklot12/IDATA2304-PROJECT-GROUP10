package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.ErrorMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
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
     * @param <T> any class implementing the {@code ByteSerializable} interface
     * @throws SerializationException thrown if serialization fails
     */
    <T extends ByteSerializable> byte[] visitList(ByteSerializableList<T> list) throws SerializationException;

    /**
     * Serializes a {@code ByteSerializableSet} object.
     *
     * @param set set to serialize
     * @return the serialized set
     * @param <T> any class implementing the {@code ByteSerializable} interface
     * @throws SerializationException thrown if serialization fails
     */
    <T extends ByteSerializable> byte[] visitSet(ByteSerializableSet<T> set) throws SerializationException;

    /**
     * Serializes a {@code ByteSerializableMap} object.
     *
     * @param map map to serialize
     * @return the serialized map
     * @param <K> any key implementing {@code ByteSerializable}
     * @param <V> any value implementing {@code ByteSerializable}
     * @throws SerializationException thrown if serialization fails
     */
    <K extends ByteSerializable, V extends ByteSerializable> byte[] visitMap(ByteSerializableMap<K, V> map) throws SerializationException;

    /**
     * Serializes a {@code RequestMessage} with parameters.
     *
     * @param request the request message to serialize
     * @param parameters the parameters to serialize, in the given order
     * @return the serialized request message
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitRequestMessage(RequestMessage request, ByteSerializable... parameters) throws SerializationException;

    /**
     * Serializes a {@code RequestMessage} with no parameters.
     *
     * @param request the request message to serialize
     * @return the serialized request message
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitRequestMessage(RequestMessage request) throws SerializationException;

    /**
     * Serializes a {@code ResponseMessage} with parameters.
     *
     * @param response the response message to serialize
     * @param parameters the parameters to serialize, in the given order
     * @return the serialized response
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitResponseMessage(ResponseMessage response, ByteSerializable... parameters) throws SerializationException;

    /**
     * Serializes a {@code ResponseMessage}.
     *
     * @param response the response message to serialize
     * @return the serialized response
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitResponseMessage(ResponseMessage response) throws SerializationException;
}
