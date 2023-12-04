package no.ntnu.network.message.serialize.visitor;

import no.ntnu.network.message.common.*;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.SimpleByteBuffer;
import no.ntnu.network.message.serialize.tool.ByteHandler;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A serializer that handles serialization of {@code ByteSerializable} objects using the serialization techniques
 * described by NOFSP.
 */
public class NofspSerializer implements ByteSerializerVisitor {
    /**
     * Creates a new NofspSerializer.
     */
    public NofspSerializer() {
    }

    @Override
    public Tlv serialize(ByteSerializable serializable) throws IOException {
        return serializable.accept(this);
    }

    @Override
    public Tlv visitInteger(ByteSerializableInteger integer) throws IOException {
        byte[] typeField = NofspSerializationConstants.INTEGER_BYTES;
        byte[] valueField = ByteHandler.intToBytes(integer.getInteger());
        return createTlv(typeField, valueField);
    }

    @Override
    public Tlv visitDouble(ByteSerializableDouble theDouble) throws IOException {
        byte[] typeField = NofspSerializationConstants.DOUBLE_BYTES;
        byte[] valueField = null;

        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.putDouble(theDouble.getDouble());
        valueField = buffer.array();

        return createTlv(typeField, valueField);
    }

    @Override
    public Tlv visitString(ByteSerializableString string) throws IOException {
        byte[] typeField = NofspSerializationConstants.STRING_BYTES;
        byte[] valueField = string.getString().getBytes(StandardCharsets.UTF_8);

        return createTlv(typeField, valueField);
    }

    @Override
    public <T extends ByteSerializable> Tlv visitList(ByteSerializableList<T> list) throws IOException {
        byte[] typeField = NofspSerializationConstants.LIST_BYTES;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (ByteSerializable item : list) {
            valueBuffer.addTlv(item.accept(this));
        }

        valueField = valueBuffer.toArray();

        return createTlv(typeField, valueField);
    }

    @Override
    public <T extends ByteSerializable> Tlv visitSet(ByteSerializableSet<T> set) throws IOException {
        byte[] typeField = NofspSerializationConstants.SET_BYTES;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (ByteSerializable item : set) {
            valueBuffer.addTlv(item.accept(this));
        }

        valueField = valueBuffer.toArray();

        return createTlv(typeField, valueField);
    }

    @Override
    public <K extends ByteSerializable, V extends ByteSerializable> Tlv visitMap(ByteSerializableMap<K, V> map) throws IOException {
        byte[] typeField = NofspSerializationConstants.MAP_BYTES;
        byte[] valueField = null;

        SimpleByteBuffer valueBuffer = new SimpleByteBuffer();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();

            valueBuffer.addTlv(key.accept(this));
            if (value != null) {
                valueBuffer.addTlv(value.accept(this));
            } else {
                valueBuffer.addTlv(createNullValueTlv());
            }
        }

        valueField = valueBuffer.toArray();

        return createTlv(typeField, valueField);
    }

    @Override
    public Tlv visitRequestMessage(RequestMessage request, ByteSerializable... parameters) throws IOException {
        List<Tlv> tlvList = getCommonRequestMessageTlvs(request);
        tlvList.add(createContainerTlv(parameters));
        return packInRequestFrame(tlvList);
    }

    @Override
    public Tlv visitRequestMessage(RequestMessage request) throws IOException {
        List<Tlv> commonRequestMessageTlvs = getCommonRequestMessageTlvs(request);
        return packInRequestFrame(commonRequestMessageTlvs);
    }

    @Override
    public Tlv visitResponseMessage(ResponseMessage response, ByteSerializable... parameters) throws IOException {
        List<Tlv> tlvList = getCommonResponseMessageTlvs(response);
        tlvList.add(createContainerTlv(parameters));
        return packInResponseFrame(tlvList);
    }

    @Override
    public Tlv visitResponseMessage(ResponseMessage response) throws IOException {
        List<Tlv> commonResponseMessageBytes = getCommonResponseMessageTlvs(response);
        return packInResponseFrame(commonResponseMessageBytes);
    }

    @Override
    public Tlv visitSensorDataMessage(SensorDataMessage message, Tlv data) throws IOException {
        Tlv clientNodeAddressTlv = serialize(message.getSerializableClientNodeAddress());
        Tlv sensorAddressTlv = serialize(message.getSerializableSensorAddress());

        return packInSensorDataFrame(createTlvList(clientNodeAddressTlv, sensorAddressTlv, data));
    }

    /**
     * Serializes multiple objects and puts them in a container TLV.
     *
     * @param serializables the serializable objects to put in container
     * @return the container TLV
     * @throws IOException thrown if serialization fails
     */
    private Tlv createContainerTlv(ByteSerializable... serializables) throws IOException {
        List<Tlv> valueField = serializeAll(serializables);
        byte[] typeField = NofspSerializationConstants.CONTAINER_TLV;

        return createTlv(typeField, valueField);
    }

    /**
     * Serializes a series of {@code ByteSerializable} objects, and returns them as a list of TLVs.
     *
     * @param serializables the serializable objects to serialize
     * @return a list of the serialized tlvs
     * @throws IOException thrown if serialization fails
     */
    private List<Tlv> serializeAll(ByteSerializable... serializables) throws IOException {
        List<Tlv> tlvs = new ArrayList<>();
        for (ByteSerializable serializable : serializables) {
            tlvs.add(serialize(serializable));
        }
        return tlvs;
    }

    /**
     * Returns the common TLVs for all {@code RequestMessage} objects.
     *
     * @param request the request message
     * @return a list of tlvs
     * @throws IOException thrown if serialization fails
     */
    private List<Tlv> getCommonRequestMessageTlvs(RequestMessage request) throws IOException {
        // first TLV contains common bytes for all control messages
        List<Tlv> commonControlMessageTlvs = getCommonControlMessageTlvs(request);

        // second TLV contains the command for the request
        Tlv commandTlv = getCommandTlv(request);

        List<Tlv> tlvs = new ArrayList<>(commonControlMessageTlvs);
        tlvs.add(commandTlv);

        return tlvs;
    }

    /**
     * Returns the common TLVs for all {@code ResponseMessage} objects.
     *
     * @param response the response message
     * @return a list of tlvs
     * @throws IOException thrown if serialization fails
     */
    private List<Tlv> getCommonResponseMessageTlvs(ResponseMessage response) throws IOException {
        // first TLVs contains common bytes for all control messages
        List<Tlv> commonControlMessageTlvs = getCommonControlMessageTlvs(response);

        // second TLV contains the status code for the response
        Tlv statusCodeTlv = serialize(response.getStatusCode());

        List<Tlv> tlvs = new ArrayList<>(commonControlMessageTlvs);
        tlvs.add(statusCodeTlv);

        return tlvs;
    }

    /**
     * Returns the common TLVs for all {@code ControlMessage} objects.
     *
     * @param controlMessage the control message
     * @return a list of tlvs
     * @throws IOException thrown if serialization fails
     */
    private List<Tlv> getCommonControlMessageTlvs(ControlMessage controlMessage) throws IOException {
        List<Tlv> tlvs = new ArrayList<>();
        tlvs.add(controlMessage.getSerializableId().accept(this));
        return tlvs;
    }

    /**
     * Returns the command TLV for any {@code RequestMessage}.
     *
     * @param request the request message
     * @return a tlv
     * @throws IOException thrown if serialization fails
     */
    private Tlv getCommandTlv(RequestMessage request) throws IOException {
        return request.getCommand().accept(this);
    }

    /**
     * Packs the content of a request message into a standard request message frame for NOFSP, and returns it as
     * a TLV.
     *
     * @param tlvs the tlvs to pack into the value field for the message frame
     * @return a serialized request frame
     * @throws IOException thrown if serialization fails
     */
    private static Tlv packInRequestFrame(List<Tlv> tlvs) throws IOException {
        byte[] typeField = NofspSerializationConstants.REQUEST_BYTES;
        return createTlv(typeField, tlvs);
    }

    /**
     * Packs the content of a response message into a standard response message frame for NOFSP, and returns it as
     * a TLV.
     *
     * @param tlvs the tlvs to pack into the value field for the message frame
     * @return a serialized response frame
     * @throws IOException thrown if serialization fails
     */
    private static Tlv packInResponseFrame(List<Tlv> tlvs) throws IOException {
        byte[] typeField = NofspSerializationConstants.RESPONSE_BYTES;
        return createTlv(typeField, tlvs);
    }

    /**
     * Packs the content of a sensor data message into a standard sensor data message frame for NOFSP, and returns it
     * as a TLV.
     *
     * @param tlvs the content of the sensor data message
     * @return a serialized sensor data frame
     * @throws IOException thrown if serialization fails
     */
    private static Tlv packInSensorDataFrame(List<Tlv> tlvs) throws IOException {
        byte[] typeField = NofspSerializationConstants.SENSOR_DATA_BYTES;
        return createTlv(typeField, tlvs);
    }

    /**
     * Creates a TLV.
     *
     * @param typeField the type field for the tlv
     * @param valueBytes the value field for the tlv
     * @return the constructed tlv
     * @throws IOException thrown if an I/O exception occurs
     */
    private static Tlv createTlv(byte[] typeField, byte[] valueBytes) throws IOException {
        byte[] lengthField = createLengthField(valueBytes.length);
        byte[] combinedBytes = ByteHandler.combineBytes(typeField, lengthField, valueBytes);

        return TlvReader.constructTlv(combinedBytes, NofspSerializationConstants.TLV_FRAME);
    }

    /**
     * Creates a TLV.
     *
     * @param typeField the type field for the tlv
     * @param tlvs the value field for the tlv - consisting of several other tlvs
     * @return the constructed tlv
     * @throws IOException thrown if an I/O exception occurs
     */
    private static Tlv createTlv(byte[] typeField, List<Tlv> tlvs) throws IOException {
        SimpleByteBuffer valueByteBuffer = new SimpleByteBuffer();
        valueByteBuffer.addTlvs(tlvs.toArray(new Tlv[0]));

        return createTlv(typeField, valueByteBuffer.toArray());
    }

    /**
     * Creates a TLV for null values.
     *
     * @return null value TLV
     */
    private static Tlv createNullValueTlv() throws IOException {
        return createTlv(NofspSerializationConstants.NULL_BYTES, new byte[0]);
    }

    /**
     * Creates a length field for a TLV, for a given length.
     *
     * @param length the length
     * @return length field containing length
     * @throws IOException thrown if serialization fails
     */
    private static byte[] createLengthField(int length) throws IOException {
        byte[] lengthField = null;

        try {
            lengthField = ByteHandler.addLeadingPadding(ByteHandler.intToBytes(length), NofspSerializationConstants.TLV_FRAME.lengthFieldLength());
        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }

        return lengthField;
    }

    /**
     * Creates a list of TLVs.
     *
     * @param tlvs tlvs to create a list from
     * @return a list of tlvs
     */
    private static List<Tlv> createTlvList(Tlv... tlvs) {
        return new ArrayList<>(Arrays.asList(tlvs));
    }
}
