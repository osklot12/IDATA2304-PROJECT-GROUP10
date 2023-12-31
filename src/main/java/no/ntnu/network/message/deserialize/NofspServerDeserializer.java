package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.*;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.deserialize.component.NofspMessageDeserializer;
import no.ntnu.network.message.deserialize.component.NofspSensorDataDeserializer;
import no.ntnu.network.message.deserialize.component.SensorDataMessageDeserializer;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.response.*;
import no.ntnu.network.message.response.error.AdlUpdateRejectedError;
import no.ntnu.network.message.response.error.DeviceInteractionFailedError;
import no.ntnu.network.message.response.error.NoSuchVirtualDeviceError;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.representation.FieldNodeInformation;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A deserializer for the central server, deserializing server-specific messages.
 */
public class NofspServerDeserializer extends NofspMessageDeserializer<ServerContext> implements SensorDataMessageDeserializer {
    private final NofspSensorDataDeserializer sensorDataDeserializer;

    /**
     * Creates a new NofspServerDeserializer.
     *
     * @param lookupTable the lookup table for device classes
     */
    public NofspServerDeserializer(DeviceLookupTable lookupTable) {
        this.sensorDataDeserializer = new NofspSensorDataDeserializer(lookupTable);
        initializeDeserializationMethods();
    }

    @Override
    public SensorDataMessage deserializeSensorData(Tlv tlv) throws IOException {
        return sensorDataDeserializer.deserializeMessage(tlv);
    }

    /**
     * Adds the implemented server message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        // requests
        addRequestMessageDeserialization(NofspSerializationConstants.REGISTER_FIELD_NODE_COMMAND, this::getRegisterFieldNodeRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND, this::getRegisterControlPanelRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.SUBSCRIBE_TO_FIELD_NODE_COMMAND, this::getSubscribeToFieldNodeRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.FIELD_NODE_POOL_PULL_COMMAND, this::getFieldNodePoolPullRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.ACTUATOR_NOTIFICATION_COMMAND, this::getActuatorNotificationRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.ACTIVATE_ACTUATOR_COMMAND, this::getServerActivateActuatorRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.UNSUBSCRIBE_FROM_FIELD_NODE_COMMAND, this::getUnsubscribeFromFieldNodeRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.DISCONNECT_CLIENT_COMMAND, this::getDisconnectRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.ASYMMETRIC_ENCRYPTION_REQUEST, this::getAsymmetricEncryptionRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.SYMMETRIC_ENCRYPTION_REQUEST, this::getSymmetricEncryptionRequest);

        // responses
        addResponseMessageDeserialization(NofspSerializationConstants.HEART_BEAT_CODE, this::getHeartBeatResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.ADL_UPDATED_CODE, this::getAdlUpdatedResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.ADL_UPDATE_REJECTED_CODE, this::getAdlUpdateRejectedError);
        addResponseMessageDeserialization(NofspSerializationConstants.VIRTUAL_ACTUATOR_UPDATED_CODE, this::getVirtualActuatorUpdatedResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.NO_SUCH_VIRTUAL_DEVICE_CODE, this::getNoSuchVirtualDeviceError);
        addResponseMessageDeserialization(NofspSerializationConstants.ACTUATOR_STATE_SET_CODE, this::getActuatorStateSetServerResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.DEVICE_INTERACTION_FAILED_CODE, this::getDeviceInteractionFailedError);
    }

    /**
     * Deserializes a {@code RegisterControlPanelRequest}.
     *
     * @param messageId the message ID
     * @param parameterReader a TlvReader holding the request parameters
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegisterFieldNodeRequest getRegisterFieldNodeRequest(int messageId, TlvReader parameterReader) throws IOException {
        RegisterFieldNodeRequest request = null;

        // deserializes fnst
        Tlv fnstTlv = parameterReader.readNextTlv();
        Map<Integer, DeviceClass> fnst = getFnst(fnstTlv);

        // deserializes fnsm
        Tlv fnsmTlv = parameterReader.readNextTlv();
        Map<Integer, Integer> fnsm = getFnsm(fnsmTlv);

        // deserializes the name
        Tlv nameTlv = parameterReader.readNextTlv();
        String name = getFieldNodeName(nameTlv);

        FieldNodeInformation fieldNodeInformation = new FieldNodeInformation(fnst, fnsm, name);

        request = new RegisterFieldNodeRequest(messageId, fieldNodeInformation);

        return request;
    }

    /**
     * Constructs an FNST from a map TLV.
     *
     * @param fnstTlv the map tlv representing the fnst
     * @return the reconstructed fnst
     * @throws IOException thrown if an I/O exception occurs
     */
    private Map<Integer, DeviceClass> getFnst(Tlv fnstTlv) throws IOException {
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFnst
                = getMapOfType(fnstTlv, ByteSerializableInteger.class, ByteSerializableString.class);
        return DataTypeConverter.getFnst(serializableFnst);
    }

    /**
     * Constructs a FNSM from a map TLV.
     *
     * @param fnsmTlv the map tlv representing to fnsm
     * @return the reconstructed fnsm
     * @throws IOException thrown if an I/O exception occurs
     */
    private Map<Integer, Integer> getFnsm(Tlv fnsmTlv) throws IOException {
        Map<Integer, Integer> fnsm = new HashMap<>();

        ByteSerializableMap<ByteSerializableInteger, ByteSerializableInteger> serializableFnsm =
                getMapOfType(fnsmTlv, ByteSerializableInteger.class, ByteSerializableInteger.class);
        serializableFnsm.forEach((key, value) -> fnsm.put(key.getInteger(), value.getInteger()));

        return fnsm;
    }

    /**
     * Constructs the field node name out of a String TLV.
     *
     * @param nameTlv the string tlv representing the field node name
     * @return the field node name, null if none is found
     */
    private String getFieldNodeName(Tlv nameTlv) {
        String name = null;

        if (nameTlv != null) {
            ByteSerializableString serializableName = getString(nameTlv);
            name = serializableName.getString();
        }

        return name;
    }

    /**
     * Deserializes a {@code RegisterControlPanelRequest}.
     *
     * @param messageId the message ID
     * @param parameterReader a TlvReader holding the request parameters
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegisterControlPanelRequest getRegisterControlPanelRequest(int messageId, TlvReader parameterReader) throws IOException {
        RegisterControlPanelRequest request = null;

        // deserializes compatibility list
        Tlv compatibilityListTlv = parameterReader.readNextTlv();
        ByteSerializableSet<ByteSerializableString> serializableCompatibilityList = getSetOfType(compatibilityListTlv, ByteSerializableString.class);

        // deserializes data sink port number
        int dataSinkPortNumber = getRegularInt(parameterReader.readNextTlv());

        request = new RegisterControlPanelRequest(messageId, DataTypeConverter.getCompatibilityList(serializableCompatibilityList), dataSinkPortNumber);

        return request;
    }

    /**
     * Deserializes a {@code SubscribeToFieldNodeRequest}.
     *
     * @param messageId the message ID
     * @param parameterReader a TlvReader holding the request parameters
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private SubscribeToFieldNodeRequest getSubscribeToFieldNodeRequest(int messageId, TlvReader parameterReader) throws IOException {
        SubscribeToFieldNodeRequest request = null;

        // deserializes field node address
        Tlv fieldNodeAddressTlv = parameterReader.readNextTlv();
        ByteSerializableInteger serializableAddress = getInteger(fieldNodeAddressTlv);

        request = new SubscribeToFieldNodeRequest(messageId, serializableAddress.getInteger());

        return request;
    }

    /**
     * Deserializes a {@code HeartbeatResponse}.
     *
     * @param messageId the message id
     * @param parameterReader the TlvReader holding the parameter tlvs
     * @return the deserialized request
     */
    private HeartbeatResponse getHeartBeatResponse(int messageId, TlvReader parameterReader) {
        return new HeartbeatResponse(messageId);
    }

    /**
     * Deserializes a {@code FieldNodePoolPullRequest}.
     *
     * @param messageId the message id
     * @param parameterReader the tlv reader holding the parameter tlvs
     * @return the deserialized request
     */
    private FieldNodePoolPullRequest getFieldNodePoolPullRequest(int messageId, TlvReader parameterReader) {
        return new FieldNodePoolPullRequest(messageId);
    }

    /**
     * Deserializes a {@code AdlUpdatedResponse}.
     *
     * @param messageId the message id
     * @param parameterReader the TlvReader holding the parameter tlvs
     * @return the deserialized request
     */
    private AdlUpdatedResponse getAdlUpdatedResponse(int messageId, TlvReader parameterReader) throws IOException {
        AdlUpdatedResponse response = null;

        // deserializes the updated adl
        ByteSerializableSet<ByteSerializableInteger> updatedAdl = getSetOfType(parameterReader.readNextTlv(),
                ByteSerializableInteger.class);

        response = new AdlUpdatedResponse(messageId, DataTypeConverter.getSetOfIntegers(updatedAdl));

        return response;
    }

    /**
     * Deserializes a {@code AdlUpdateRejectedError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception is thrown
     */
    private AdlUpdateRejectedError getAdlUpdateRejectedError(int messageId, TlvReader parameterReader) throws IOException {
        AdlUpdateRejectedError response = null;

        // deserializes the description
        String description = getRegularString(parameterReader.readNextTlv());

        response = new AdlUpdateRejectedError(messageId, description);

        return response;
    }

    /**
     * Deserializes a {@code ActuatorNotificationRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private ActuatorNotificationRequest getActuatorNotificationRequest(int messageId, TlvReader parameterReader) throws IOException {
        ActuatorNotificationRequest request = null;

        // deserializes the actuator address
        int actuatorAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes the new state
        int newState = getRegularInt(parameterReader.readNextTlv());

        request = new ActuatorNotificationRequest(messageId, actuatorAddress, newState);

        return request;
    }

    /**
     * Deserializes a {@code VirtualActuatorUpdatedResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private VirtualActuatorUpdatedResponse getVirtualActuatorUpdatedResponse(int messageId, TlvReader parameterReader) throws IOException {
        return new VirtualActuatorUpdatedResponse(messageId);
    }

    /**
     * Deserializes a {@code NoSuchVirtualDeviceError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private NoSuchVirtualDeviceError getNoSuchVirtualDeviceError(int messageId, TlvReader parameterReader) throws IOException {
        NoSuchVirtualDeviceError response = null;

        // deserializes the error description
        String description = getRegularString(parameterReader.readNextTlv());

        response = new NoSuchVirtualDeviceError(messageId, description);

        return response;
    }

    /**
     * Deserializes a {@code ServerActivateActuatorRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private ServerActivateActuatorRequest getServerActivateActuatorRequest(int messageId, TlvReader parameterReader) throws IOException {
        ServerActivateActuatorRequest request = null;

        // deserializes the field node address
        int fieldNodeAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes the actuator address
        int actuatorAddress = getRegularInt(parameterReader.readNextTlv());

        // deserializes the new state
        int newState = getRegularInt(parameterReader.readNextTlv());

        request = new ServerActivateActuatorRequest(messageId, fieldNodeAddress, actuatorAddress, newState);

        return request;
    }

    /**
     * Deserializes a {@code ActuatorStateSetServerResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     */
    private ActuatorStateSetServerResponse getActuatorStateSetServerResponse(int messageId, TlvReader parameterReader) {
        return new ActuatorStateSetServerResponse(messageId);
    }

    /**
     * Deserializes a {@code DeviceInteractionFailedError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private DeviceInteractionFailedError getDeviceInteractionFailedError(int messageId, TlvReader parameterReader) throws IOException {
        DeviceInteractionFailedError response = null;

        // deserializes the error description
        String description = getRegularString(parameterReader.readNextTlv());

        response = new DeviceInteractionFailedError(messageId, description);

        return response;
    }

    /**
     * Deserializes a {@code UnsubscribeFromFieldNodeRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private UnsubscribeFromFieldNodeRequest getUnsubscribeFromFieldNodeRequest(int messageId, TlvReader parameterReader) throws IOException {
        UnsubscribeFromFieldNodeRequest request = null;

        // deserializes the field node address
        int fieldNodeAddress = getRegularInt(parameterReader.readNextTlv());

        request = new UnsubscribeFromFieldNodeRequest(messageId, fieldNodeAddress);

        return request;
    }

    /**
     * Deserializes a {@code DisconnectRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     */
    private DisconnectRequest getDisconnectRequest(int messageId, TlvReader parameterReader) {
        return new DisconnectRequest(messageId);
    }

    /**
     * Deserializes a {@code AsymmetricEncryptionRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     */
    private AsymmetricEncryptionRequest getAsymmetricEncryptionRequest(int messageId, TlvReader parameterReader) {
        return new AsymmetricEncryptionRequest(messageId);
    }

    /**
     * Deserializes a {@code SymmetricEncryptionRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the parameter tlvs
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private SymmetricEncryptionRequest getSymmetricEncryptionRequest(int messageId, TlvReader parameterReader) throws IOException {
        SymmetricEncryptionRequest request = null;

        // deserializes the secret key
        SecretKey key = getAESSecretKey(parameterReader.readNextTlv()).key();

        request = new SymmetricEncryptionRequest(messageId, key);

        return request;
    }
}
