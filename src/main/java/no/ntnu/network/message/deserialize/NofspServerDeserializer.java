package no.ntnu.network.message.deserialize;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A deserializer for the central server, deserializing server-specific messages.
 */
public class NofspServerDeserializer extends NofspDeserializer implements MessageDeserializer<ServerContext> {
    @Override
    public Message<ServerContext> deserializeMessage(byte[] bytes) throws IOException {
        if (bytes == null) {
            throw new IOException("Cannot identify type field, because bytes is null.");
        }

        byte[] valueField = TlvReader.getValueField(bytes, TLV_FRAME);

        Message<ServerContext> message = null;

        if (tlvOfType(bytes, NofspSerializationConstants.REQUEST_BYTES)) {
            // type field: request message
            message = getRequestMessage(valueField);

        } else if (tlvOfType(bytes, NofspSerializationConstants.RESPONSE_BYTES)) {
            // type field: response message
            message = getResponseMessage(valueField);

        }

        return message;
    }

    /**
     * Deserializes a request TLV of bytes into a {@code ServerMessage}.
     *
     * @param bytes tlv to deserialize
     * @return the request object
     * @throws IOException thrown if an I/O exception occurs
     */
    private Message<ServerContext> getRequestMessage(byte[] bytes) throws IOException {
        Message<ServerContext> request = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        // first TLV holds the message ID
        ByteSerializableInteger messageId = getMessageId(tlvReader);

        // second TLV holds the command
        ByteSerializableString command = getRequestMessageCommand(tlvReader);

        // third TLV holds the request parameters, which are also TLVs
        TlvReader parameterReader = new TlvReader(tlvReader.readNextTlv(), TLV_FRAME);

        if (stringEquals(command, NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND)) {
            // register control panel request
            request = getRegisterControlPanelRequest(messageId, parameterReader);

        }

        return request;
    }

    /**
     * Deserializes a response TLV of bytes into a Message.
     *
     * @param bytes tlv to deserialize
     * @return the response object
     * @throws IOException thrown if an I/O exception occurs
     */
    private Message<ServerContext> getResponseMessage(byte[] bytes) throws IOException {
        Message<ServerContext> response = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        // first TLV holds the message ID
        ByteSerializableInteger messageId = getMessageId(tlvReader);

        // second TLV holds the status code
        ByteSerializableInteger statusCode = getStatusCode(tlvReader);

        // third TLV holds the request parameters, which are also TLVs
        byte[] parameterTlv = tlvReader.readNextTlv();

        if (integerEquals(statusCode, NofspSerializationConstants.HEART_BEAT_RESPONSE)) {
            // heart beat response
            response = new HeartbeatResponse(messageId.getInteger());
        }

        return response;
    }

    /**
     * Deserializes a {@code RegisterControlPanelRequest}.
     *
     * @param messageId the message ID
     * @param parameterReader a TlvReader holding the request parameters
     * @return the deserialized request
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegisterControlPanelRequest getRegisterControlPanelRequest(ByteSerializableInteger messageId, TlvReader parameterReader) throws IOException {
        RegisterControlPanelRequest request = null;

        // deserializes compatibility list
        byte[] compatibilityListTlv = parameterReader.readNextTlv();
        byte[] compatibilityListValueField = TlvReader.getValueField(compatibilityListTlv, TLV_FRAME);
        ByteSerializableSet<ByteSerializableString> compatibilityList = getSet(compatibilityListValueField, ByteSerializableString.class);
        request = new RegisterControlPanelRequest(messageId.getInteger(), makeDeviceClassSet(compatibilityList));

        return request;
    }

    /**
     * Creates a {@code Set} of DeviceClass constants out of a {@code ByteSerializableSet} of strings.
     *
     * @param serializableSet the set to extract strings from
     * @return a set with device class constants
     */
    private Set<DeviceClass> makeDeviceClassSet(ByteSerializableSet<ByteSerializableString> serializableSet) {
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
}
