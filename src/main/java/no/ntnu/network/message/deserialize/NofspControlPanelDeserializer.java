package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.TlvReader;

import java.io.IOException;

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

        if (tlvOfType(bytes, NofspSerializationConstants.RESPONSE_BYTES)) {
            // type: response message
            message = getResponseMessage(valueField);
        }

        return message;
    }

    /**
     * Deserializes a response TLV of bytes into a {@code ControlPanelMessage}.
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
        TlvReader parameterReader = new TlvReader(tlvReader.readNextTlv(), TLV_FRAME);

        if (integerEquals(statusCode, NofspSerializationConstants.NODE_REGISTRATION_CONFIRMED_CODE)) {
            // node registration confirmation response
            response = getRegistrationConfirmedResponse(messageId, parameterReader);

        }

        return response;
    }

    /**
     * Deserializes a {@code RegistrationConfirmedResponse}.
     *
     * @param messageId the message ID
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegistrationConfirmationResponse<ControlPanelContext> getRegistrationConfirmedResponse(ByteSerializableInteger messageId, TlvReader parameterReader) throws IOException {
        RegistrationConfirmationResponse<ControlPanelContext> response = null;

        // deserializes the node address
        byte[] nodeAddressTlv = parameterReader.readNextTlv();
        byte[] nodeAddressValueField = TlvReader.getValueField(nodeAddressTlv, TLV_FRAME);
        ByteSerializableInteger nodeAddress = getInteger(nodeAddressValueField);
        response = new RegistrationConfirmationResponse(messageId.getInteger(), nodeAddress.getInteger());

        return response;
    }
}