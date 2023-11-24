package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableFieldNodePool;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.component.NofspClientMessageDeserializer;
import no.ntnu.network.message.request.ServerFnsmNotificationRequest;
import no.ntnu.network.message.response.FieldNodePoolResponse;
import no.ntnu.network.message.response.SubscribedToFieldNodeResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.util.Map;

/**
 * A deserializer for deserializing control panel messages.
 */
public class NofspControlPanelDeserializer extends NofspClientMessageDeserializer<ControlPanelContext> {
    /**
     * Creates a new NofspControlPanelDeserializer.
     */
    public NofspControlPanelDeserializer() {
        super();

        initializeDeserializationMethods();
    }

    /**
     * Adds the implemented control panel message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        // requests
        addRequestMessageDeserialization(NofspSerializationConstants.FNSM_NOTIFICATION_COMMAND, this::getServerFnsmNotificationRequest);

        // responses
        addResponseMessageDeserialization(NofspSerializationConstants.FIELD_NODE_POOL_CODE, this::getFieldNodePoolResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.SUBSCRIBED_TO_FIELD_NODE_CODE, this::getSubscribeToFieldNodeResponse);
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
    private SubscribedToFieldNodeResponse getSubscribeToFieldNodeResponse(int messageId, TlvReader parameterReader) throws IOException {
        SubscribedToFieldNodeResponse response = null;

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

        return new SubscribedToFieldNodeResponse(messageId, fnst, fnsm, name);
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

        // deseializes the new state
        int newState = getRegularInt(parameterReader.readNextTlv());

        request = new ServerFnsmNotificationRequest(messageId, fieldNodeAddress, actuatorAddress, newState);

        return request;
    }
}
