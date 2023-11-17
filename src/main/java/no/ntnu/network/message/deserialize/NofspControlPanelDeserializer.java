package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.io.IOException;
import java.lang.reflect.Parameter;

/**
 * A deserializer for deserializing control panel messages.
 */
public class NofspControlPanelDeserializer extends NofspDeserializer implements MessageDeserializer<ControlPanelContext> {
    @Override
    public Message<ControlPanelContext> deserializeMessage(byte[] bytes) throws IOException {
        if (bytes == null) {
            throw new IOException("Cannot identify type field, because bytes is null.");
        }

        byte[] valueField = TlvReader.getValueField(bytes, TLV_FRAME);

        Message<ControlPanelContext> message = null;

        if (tlvOfType(bytes, NofspSerializationConstants.REQUEST_BYTES)) {
            // type: request message
            message = getRequestMessage(valueField);
        }

        if (tlvOfType(bytes, NofspSerializationConstants.RESPONSE_BYTES)) {
            // type: response message
            message = getResponseMessage(valueField);
        }



        return message;
    }

    /**
     * Deserializes a request TLV of bytes into a Message.
     *
     * @param bytes tlv to deserialize
     * @return the request object
     * @throws IOException thrown if an I/O exception occurs
     */
    private Message<ControlPanelContext> getRequestMessage(byte[] bytes) throws IOException {
        Message<ControlPanelContext> request = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        // first TLV holds the message ID
        ByteSerializableInteger messageId = getMessageId(tlvReader);

        // second TLV holds the command
        ByteSerializableString command = getRequestMessageCommand(tlvReader);

        // third TLV holds the response parameters, which are also TLVs
        byte[] parameterTlv = tlvReader.readNextTlv();

        if (stringEquals(command, NofspSerializationConstants.HEART_BEAT)) {
            // heartbeat request
            request = new HeartbeatRequest<>(messageId.getInteger());
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
    private Message<ControlPanelContext> getResponseMessage(byte[] bytes) throws IOException {
        Message<ControlPanelContext> response = null;

        TlvReader tlvReader = new TlvReader(bytes, TLV_FRAME);

        // first TLV holds the message ID
        ByteSerializableInteger messageId = getMessageId(tlvReader);

        // second TLV holds the status code
        ByteSerializableInteger statusCode = getStatusCode(tlvReader);

        // third TLV holds the response parameters, which are also TLVs
        byte[] parameterTlv = tlvReader.readNextTlv();

        if (integerEquals(statusCode, NofspSerializationConstants.NODE_REGISTRATION_CONFIRMED_CODE)) {
            // node registration confirmation response
            response = getRegistrationConfirmedResponse(messageId, parameterTlv);

        } else if (integerEquals(statusCode, NofspSerializationConstants.NODE_REGISTRATION_DECLINED_CODE)) {
            // node registration declined response
            response = getRegistrationDeclinedError(messageId, parameterTlv);
        }

        return response;
    }

    private RegistrationDeclinedError<ControlPanelContext> getRegistrationDeclinedError(ByteSerializableInteger messageId, byte[] parameterTlv) throws IOException {
        RegistrationDeclinedError<ControlPanelContext> response = null;

        TlvReader parameterReader = new TlvReader(parameterTlv, TLV_FRAME);
        // deserializes the description
        byte[] descriptionTlv = parameterReader.readNextTlv();
        byte[] descriptionValueField = TlvReader.getValueField(descriptionTlv, TLV_FRAME);
        ByteSerializableString description = getString(descriptionValueField);
        response = new RegistrationDeclinedError<>(messageId.getInteger(), description.toString());

        return response;
    }

    /**
     * Deserializes a {@code RegistrationConfirmedResponse}.
     *
     * @param messageId the message ID
     * @param parameterTlv the parameter tlv
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegistrationConfirmationResponse<ControlPanelContext> getRegistrationConfirmedResponse(ByteSerializableInteger messageId, byte[] parameterTlv) throws IOException {
        RegistrationConfirmationResponse<ControlPanelContext> response = null;

        TlvReader parameterReader = new TlvReader(parameterTlv, TLV_FRAME);
        // deserializes the node address
        byte[] nodeAddressTlv = parameterReader.readNextTlv();
        byte[] nodeAddressValueField = TlvReader.getValueField(nodeAddressTlv, TLV_FRAME);
        ByteSerializableInteger nodeAddress = getInteger(nodeAddressValueField);
        response = new RegistrationConfirmationResponse<>(messageId.getInteger(), nodeAddress.getInteger());

        return response;
    }
}
