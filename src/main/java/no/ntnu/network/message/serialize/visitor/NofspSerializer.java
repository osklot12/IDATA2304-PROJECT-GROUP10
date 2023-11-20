package no.ntnu.network.message.serialize.visitor;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.request.RegisterFieldNodeRequest;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.ErrorMessage;
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

    /**
     * Creates a new NofspSerializer.
     */
    public NofspSerializer() {}

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
    public <T extends ByteSerializable> byte[] visitSet(ByteSerializableSet<T> set) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.SET_BYTES;
        byte[] lengthField = null;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (ByteSerializable item : set) {
            valueBuffer.addBytes(item.accept(this));
        }

        valueField = valueBuffer.toArray();
        lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    @Override
    public <K extends ByteSerializable, V extends ByteSerializable> byte[] visitMap(ByteSerializableMap<K, V> map) throws SerializationException {
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
    public byte[] visitRegisterFieldNodeRequest(RegisterFieldNodeRequest request) throws SerializationException {
        byte[] commonRequestMessageBytes = getCommonRequestMessageBytes(request);
        byte[] parameterTlv = createContainerTlv(request.getSerializableFnst(), request.getSerializableFnsm(),
                request.getSerializableName());

        return packInRequestFrame(ByteHandler.combineBytes(commonRequestMessageBytes, parameterTlv));
    }

    @Override
    public byte[] visitRegisterControlPanelRequest(RegisterControlPanelRequest request) throws SerializationException {
        byte[] commonRequestMessageBytes = getCommonRequestMessageBytes(request);
        byte[] parameterTlv = createContainerTlv(request.getSerializableCompatibilityList());

        return packInRequestFrame(ByteHandler.combineBytes(commonRequestMessageBytes, parameterTlv));
    }

    @Override
    public byte[] visitRegistrationConfirmationResponse(RegistrationConfirmationResponse<?> response) throws SerializationException {
        byte[] commonResponseMessageBytes = getCommonResponseMessageBytes(response);
        byte[] parameterTlv = createContainerTlv(response.getNodeAddress());

        return packInResponseFrame(ByteHandler.combineBytes(commonResponseMessageBytes, parameterTlv));
    }

    @Override
    public byte[] visitErrorMessage(ErrorMessage errorMessage) throws SerializationException {
        byte[] commonResponseMessageBytes = getCommonResponseMessageBytes(errorMessage);
        byte[] parameterTlv = createContainerTlv(errorMessage.getDescription());

        return packInResponseFrame(ByteHandler.combineBytes(commonResponseMessageBytes, parameterTlv));
    }

    @Override
    public <C extends ClientContext> byte[] visitHeartbeatRequest(HeartbeatRequest<C> request) throws SerializationException {
        byte[] commonRequestMessageBytes = getCommonRequestMessageBytes(request);

        return packInRequestFrame(commonRequestMessageBytes);
    }

    @Override
    public byte[] visitHeartbeatResponse(HeartbeatResponse response) throws SerializationException {
        byte[] commonResponseMessageBytes = getCommonResponseMessageBytes(response);

        return packInResponseFrame(commonResponseMessageBytes);
    }

    /**
     * Serializes multiple objects and puts them in a container TLV.
     *
     * @param serializables the serializable objects to put in container
     * @return the container TLV
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] createContainerTlv(ByteSerializable... serializables) throws SerializationException {
        byte[] valueField = serializeAll(serializables);
        byte[] typeField = NofspSerializationConstants.CONTAINER_TLV;
        byte[] lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    /**
     * Serializes a series of {@code ByteSerializable} objects, and puts them after one another as TLVs of bytes.
     *
     * @param serializables the serializable objects to serialize
     * @return an array of bytes representing the serialized objects
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] serializeAll(ByteSerializable... serializables) throws SerializationException {
        SimpleByteBuffer byteBuffer = new SimpleByteBuffer();

        for (ByteSerializable serializable : serializables) {
            byteBuffer.addBytes(serialize(serializable));
        }

        return byteBuffer.toArray();
    }

    /**
     * Returns the common TLVs for all {@code RequestMessage} objects in bytes.
     *
     * @param request the request message
     * @return the serialized bytes
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] getCommonRequestMessageBytes(RequestMessage request) throws SerializationException {
        // first TLV contains common bytes for all control messages
        byte[] commonControlMessageBytes = getCommonControlMessageBytes(request);

        // second TLV contains the command for the request
        byte[] commandTlv = getCommandTlv(request);

        return ByteHandler.combineBytes(commonControlMessageBytes, commandTlv);
    }

    /**
     * Returns the common TLVs for all {@code ResponseMessage} objects in bytes.
     *
     * @param response the response message
     * @return the serialized bytes
     * @throws SerializationException thrown if serialization fails
     */
    private byte[] getCommonResponseMessageBytes(ResponseMessage response) throws SerializationException {
        // first TLV contains common bytes for all control messages
        byte[] commonControlMessageBytes = getCommonControlMessageBytes(response);

        // second TLV contains the status code for the response
        byte[] statusCodeTlv = serialize(response.getStatusCode());

        return ByteHandler.combineBytes(commonControlMessageBytes, statusCodeTlv);
    }

    /**
     * Returns the common TLVs for all {@code ControlMessage} objects in bytes.
     *
     * @param controlMessage the control message
     * @return the serialized bytes
     */
    private byte[] getCommonControlMessageBytes(ControlMessage controlMessage) throws SerializationException {
        return controlMessage.getId().accept(this);
    }

    /**
     * Returns the command TLV for any {@code RequestMessage} in bytes.
     *
     * @param request the request message
     * @return the serialized bytes
     */
    private byte[] getCommandTlv(RequestMessage request) throws SerializationException {
        return request.getCommand().accept(this);
    }

    /**
     * Packs the content of a request message into a standard message frame for NOFSP, serializes it returns and it
     * in bytes.
     *
     * @param valueField the value field of the frame - containing all fields for the request message
     * @return a serialized request frame
     * @throws SerializationException thrown if serialization fails
     */
    private static byte[] packInRequestFrame(byte[] valueField) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.REQUEST_BYTES;
        byte[] lengthField = createLengthField(valueField.length);

        return createTlv(typeField, lengthField, valueField);
    }

    /**
     * Packs the content of a response message into a standard message frame for NOFSP, serializes it and returns
     * it in bytes.
     *
     * @param valueField the value field of the frame - containing all fields for the response message
     * @return a serialized response frame
     * @throws SerializationException thrown if serialization fails
     */
    private static byte[] packInResponseFrame(byte[] valueField) throws SerializationException {
        byte[] typeField = NofspSerializationConstants.RESPONSE_BYTES;
        byte[] lengthField = createLengthField(valueField.length);

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

        return serializableString.accept(this);
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
