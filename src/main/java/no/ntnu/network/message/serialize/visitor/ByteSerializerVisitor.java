package no.ntnu.network.message.serialize.visitor;

import no.ntnu.network.message.common.*;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;

import java.io.IOException;

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
     * @throws IOException thrown if serialization fails
     */
    Tlv serialize(ByteSerializable serializable) throws IOException;

    /**
     * Serializes a {@code ByteSerializableInteger} object.
     *
     * @param integer integer to serialize
     * @return the serialized integer
     * @throws IOException thrown if serialization fails
     */
    Tlv visitInteger(ByteSerializableInteger integer) throws IOException;

    /**
     * Serializes a {@code ByteSerializableDouble} object.
     *
     * @param theDouble double to serialize
     * @return the serialized double
     * @throws IOException thrown if serialization fails
     */
    Tlv visitDouble(ByteSerializableDouble theDouble) throws IOException;

    /**
     * Serializes a {@code ByteSerializableString} object.
     *
     * @param string string to serialize
     * @return the serialized string
     * @throws IOException thrown if serialization fails
     */
    Tlv visitString(ByteSerializableString string) throws IOException;

    /**
     * Serializes a {@code ByteSerializableList} object.
     *
     * @param list list to serialize
     * @return the serialized list
     * @param <T> any class implementing the {@code ByteSerializable} interface
     * @throws IOException thrown if serialization fails
     */
    <T extends ByteSerializable> Tlv visitList(ByteSerializableList<T> list) throws IOException;

    /**
     * Serializes a {@code ByteSerializableSet} object.
     *
     * @param set set to serialize
     * @return the serialized set
     * @param <T> any class implementing the {@code ByteSerializable} interface
     * @throws IOException thrown if serialization fails
     */
    <T extends ByteSerializable> Tlv visitSet(ByteSerializableSet<T> set) throws IOException;

    /**
     * Serializes a {@code ByteSerializableMap} object.
     *
     * @param map map to serialize
     * @return the serialized map
     * @param <K> any key implementing {@code ByteSerializable}
     * @param <V> any value implementing {@code ByteSerializable}
     * @throws IOException thrown if serialization fails
     */
    <K extends ByteSerializable, V extends ByteSerializable> Tlv visitMap(ByteSerializableMap<K, V> map) throws IOException;

    /**
     * Serializes a {@code RequestMessage} with parameters.
     *
     * @param request the request message to serialize
     * @param parameters the parameters to serialize, in the given order
     * @return the serialized request message
     * @throws IOException thrown if serialization fails
     */
    Tlv visitRequestMessage(RequestMessage request, ByteSerializable... parameters) throws IOException;

    /**
     * Serializes a {@code RequestMessage} with no parameters.
     *
     * @param request the request message to serialize
     * @return the serialized request message
     * @throws IOException thrown if serialization fails
     */
    Tlv visitRequestMessage(RequestMessage request) throws IOException;

    /**
     * Serializes a {@code ResponseMessage} with parameters.
     *
     * @param response the response message to serialize
     * @param parameters the parameters to serialize, in the given order
     * @return the serialized response
     * @throws IOException thrown if serialization fails
     */
    Tlv visitResponseMessage(ResponseMessage response, ByteSerializable... parameters) throws IOException;

    /**
     * Serializes a {@code ResponseMessage}.
     *
     * @param response the response message to serialize
     * @return the serialized response
     * @throws IOException thrown if serialization fails
     */
    Tlv visitResponseMessage(ResponseMessage response) throws IOException;

    /**
     * Serializes a {@code SensorDataMessage}.
     *
     * @param message the sensor data message to serialize
     * @param data the data captured
     * @return the serialized sensor data message
     * @throws IOException thrown if serialization fails
     */
    Tlv visitSensorDataMessage(SensorDataMessage message, Tlv data) throws IOException;
}
