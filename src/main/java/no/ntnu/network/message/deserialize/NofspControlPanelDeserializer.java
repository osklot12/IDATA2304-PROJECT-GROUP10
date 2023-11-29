package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.deserialize.component.NofspClientMessageDeserializer;
import no.ntnu.network.message.deserialize.component.NofspSensorDataDeserializer;
import no.ntnu.network.message.deserialize.component.SensorDataMessageDeserializer;
import no.ntnu.network.message.request.ServerFnsmNotificationRequest;
import no.ntnu.network.message.response.ActuatorStateSetControlPanelResponse;
import no.ntnu.network.message.response.FieldNodePoolResponse;
import no.ntnu.network.message.response.SubscribedToFieldNodeResponse;
import no.ntnu.network.message.response.UnsubscribedFromFieldNodeResponse;
import no.ntnu.network.message.response.error.FieldNodeUnreachableError;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.Map;

/**
 * A deserializer for deserializing control panel messages.
 */
public class NofspControlPanelDeserializer extends NofspClientMessageDeserializer<ControlPanelContext> implements SensorDataMessageDeserializer {
    private final NofspSensorDataDeserializer sensorDataDeserializer;

    /**
     * Creates a new NofspControlPanelDeserializer.
     */
    public NofspControlPanelDeserializer(DeviceLookupTable lookupTable) {
        super();

        this.sensorDataDeserializer = new NofspSensorDataDeserializer(lookupTable);
        initializeDeserializationMethods();
    }

    @Override
    public SensorDataMessage deserializeSensorData(Tlv tlv) throws IOException {
        return sensorDataDeserializer.deserializeMessage(tlv);
    }

    /**
     * Adds the implemented control panel message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        // requests
        addRequestMessageDeserialization(NofspSerializationConstants.FNSM_NOTIFICATION_COMMAND, this::getServerFnsmNotificationRequest);

        // responses
        addResponseMessageDeserialization(NofspSerializationConstants.FIELD_NODE_POOL_CODE, this::getFieldNodePoolResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.SUBSCRIBED_TO_FIELD_NODE_CODE, this::getSubscribedToFieldNodeResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.ACTUATOR_STATE_SET_CODE, this::getActuatorStateSetControlPanelResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.FIELD_NODE_UNREACHABLE_CODE, this::getFieldNodeUnreachableError);
        addResponseMessageDeserialization(NofspSerializationConstants.UNSUBSCRIBED_FROM_FIELD_NODE_CODE, this::getUnsubscribedFromFieldNodeResponse);
    }

    /**
     * Deserializes a {@code FieldNodePoolResponse}.
     *
     * @param messageId       the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private FieldNodePoolResponse getFieldNodePoolResponse(int messageId, TlvReader parameterReader) throws IOException {
        FieldNodePoolResponse response = null;

        // deserializes field node pool
        Tlv fieldNodePoolTlv = parameterReader.readNextTlv();
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFieldNodePool =
                getMapOfType(fieldNodePoolTlv, ByteSerializableInteger.class, ByteSerializableString.class);
        Map<Integer, String> fieldNodePool = DataTypeConverter.getFieldNodePool(serializableFieldNodePool);

        response = new FieldNodePoolResponse(messageId, fieldNodePool);

        return response;
    }

    /**
     * Deserializes a {@code SubscribeToFieldNodeResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private SubscribedToFieldNodeResponse getSubscribedToFieldNodeResponse(int messageId, TlvReader parameterReader) throws IOException {
        SubscribedToFieldNodeResponse response = null;

        // deserializes the field node address
        int fieldNodeAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes FNST
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFnst
                = getMapOfType(parameterReader.readNextTlv(), ByteSerializableInteger.class, ByteSerializableString.class);
        Map<Integer, DeviceClass> fnst = DataTypeConverter.getFnst(serializableFnst);

        // deserializes FNSM
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> serializableFnsm
                = getMapOfType(parameterReader.readNextTlv(), ByteSerializableInteger.class, ByteSerializableInteger.class);
        Map<Integer, Integer> fnsm = DataTypeConverter.getFnsm(serializableFnsm);

        // deserializes name
        String name = getRegularString(parameterReader.readNextTlv());

        response = new SubscribedToFieldNodeResponse(messageId, fieldNodeAddress, fnst, fnsm, name);

        return response;
    }

    /**
     * Deserializes a {@code ServerFnsmNotificationRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private ServerFnsmNotificationRequest getServerFnsmNotificationRequest(int messageId, TlvReader parameterReader) throws IOException {
        ServerFnsmNotificationRequest request = null;

        // deserializes the field node address
        int fieldNodeAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes the actuator address
        int actuatorAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes the new state
        int newState = getRegularInt(parameterReader.readNextTlv());

        request = new ServerFnsmNotificationRequest(messageId, fieldNodeAddress, actuatorAddress, newState);

        return request;
    }

    /**
     * Deserializes a {@code ActuatorStateSetControlPanelResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private ActuatorStateSetControlPanelResponse getActuatorStateSetControlPanelResponse(int messageId, TlvReader parameterReader) throws IOException {
        return new ActuatorStateSetControlPanelResponse(messageId);
    }

    /**
     * Deserializes a {@code FieldNodeUnreachableError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private FieldNodeUnreachableError getFieldNodeUnreachableError(int messageId, TlvReader parameterReader) throws IOException {
        FieldNodeUnreachableError response = null;

        // deserializes the error description
        String description = getRegularString(parameterReader.readNextTlv());

        response = new FieldNodeUnreachableError(messageId, description);

        return response;
    }

    /**
     * Deserializes a {@code UnsubscribedFromFieldNodeResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the message parameters
     * @return the deserialized response
     */
    private UnsubscribedFromFieldNodeResponse getUnsubscribedFromFieldNodeResponse(int messageId, TlvReader parameterReader) throws IOException {
        UnsubscribedFromFieldNodeResponse response = null;

        // deserializes the field node address
        int fieldNodeAddress = getRegularInt(parameterReader.readNextTlv());

        response = new UnsubscribedFromFieldNodeResponse(messageId, fieldNodeAddress);

        return response;
    }
}
