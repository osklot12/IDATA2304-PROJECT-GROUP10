package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
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
     * Serializes a {@code RegisterControlPanelRequest} object.
     *
     * @param request the request to serialize
     * @return the serialized request
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitRegisterControlPanelRequest(RegisterControlPanelRequest request) throws SerializationException;

    /**
     * Serializes a {@code RegistrationConfirmationResponse} object.
     *
     * @param response the response to serialize
     * @return the serialized response
     * @param <C> any client context
     * @throws SerializationException thrown if serialization fails
     */
    <C extends ClientContext> byte[] visitRegistrationConfirmationResponse(RegistrationConfirmationResponse<C> response) throws SerializationException;

    /**
     * Serializes a {@code HeartbeatRequest} object
     *
     * @param request the request to serialize
     * @return the serialized request
     * @param <C> any client context
     * @throws SerializationException thrown if serialization fails
     */
    <C extends ClientContext> byte[] visitHeartbeatRequest(HeartbeatRequest<C> request) throws SerializationException;

    /**
     * Serializes a {@code HeartbeatResponse} object.
     *
     * @param response the response to serialize
     * @return the serialized response
     * @throws SerializationException thrown if serialization fails
     */
    byte[] visitHeartbeatResponse(HeartbeatResponse response) throws SerializationException;
}
