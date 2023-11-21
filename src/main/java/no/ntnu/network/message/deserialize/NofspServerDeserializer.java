package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.component.NofspMessageDeserializer;
import no.ntnu.network.message.request.FieldNodePoolPullRequest;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.request.RegisterFieldNodeRequest;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A deserializer for the central server, deserializing server-specific messages.
 */
public class NofspServerDeserializer extends NofspMessageDeserializer<ServerContext> {
    /**
     * Creates a new NofspServerDeserializer.
     */
    public NofspServerDeserializer() {
        super();

        initializeDeserializationMethods();
    }

    /**
     * Adds the implemented server message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        addRequestMessageDeserialization(NofspSerializationConstants.REGISTER_FIELD_NODE_COMMAND, this::getRegisterFieldNodeRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND, this::getRegisterControlPanelRequest);
        addRequestMessageDeserialization(NofspSerializationConstants.FIELD_NODE_POOL_PULL, this::getFieldNodePoolPullRequest);
        addResponseMessageDeserialization(NofspSerializationConstants.HEART_BEAT_CODE, this::getHeartBeatResponse);
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

        // deserializes the name (only if one is found)
        Tlv nameTlv = parameterReader.readNextTlv();
        String name = getFieldNodeName(nameTlv);

        request = new RegisterFieldNodeRequest(fnst, fnsm, name);

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
        return transformFnst(serializableFnst);
    }

    /**
     * Creates a normal FNST from a serializable FNST.
     *
     * @param serializableFnst the serializable fnst
     * @return a normal fnst
     */
    private Map<Integer, DeviceClass> transformFnst(ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> serializableFnst) {
        Map<Integer, DeviceClass> fnst = new HashMap<>();

        serializableFnst.forEach((key, value) -> {
            int deviceAddress = key.getInteger();
            DeviceClass deviceClass = DeviceClass.getDeviceClass(value.getString());
            if (deviceClass != null) {
                fnst.put(deviceAddress, deviceClass);
            }
        });

        return fnst;
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
        serializableFnsm.forEach((key, value) -> {
            fnsm.put(key.getInteger(), value.getInteger());
        });

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
        ByteSerializableSet<ByteSerializableString> compatibilityList = getSetOfType(compatibilityListTlv, ByteSerializableString.class);

        request = new RegisterControlPanelRequest(messageId, createDeviceClassSet(compatibilityList));

        return request;
    }

    /**
     * Creates a {@code Set} of DeviceClass constants out of a {@code ByteSerializableSet} of strings.
     *
     * @param serializableSet the set to extract strings from
     * @return a set with device class constants
     */
    private Set<DeviceClass> createDeviceClassSet(ByteSerializableSet<ByteSerializableString> serializableSet) {
        Set<DeviceClass> deviceClassSet = new HashSet<>();

        serializableSet.forEach(
                item -> {
                    DeviceClass deviceClass = DeviceClass.getDeviceClass(item.getString());
                    if (deviceClass != null) {
                        deviceClassSet.add(deviceClass);
                    }
                }
        );

        return deviceClassSet;
    }

    /**
     * Deserializes a {@code HeartbeatResponse}.
     *
     * @param messageId the message id
     * @param parameterReader the tlv reader holding the parameter tlvs
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
}
