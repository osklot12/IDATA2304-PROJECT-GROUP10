package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;

/**
 * A message deserializer for client messages.
 *
 * @param <C> any client message context
 */
public abstract class NofspClientMessageDeserializer<C extends ClientContext> extends NofspMessageDeserializer<C> {
    /**
     * Creates a new ClientMessageDeserializer.
     */
    protected NofspClientMessageDeserializer() {
        super();

        initializeDeserializationMethods();
    }

    /**
     * Adds the implemented server message deserialization methods to the lookup tables.
     */
    private void initializeDeserializationMethods() {
        addResponseMessageDeserialization(NofspSerializationConstants.NODE_REGISTRATION_CONFIRMED_CODE, this::getRegistrationConfirmedResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.NODE_REGISTRATION_DECLINED_CODE, this::getRegistrationDeclinedError);
        addRequestMessageDeserialization(NofspSerializationConstants.HEART_BEAT, this::getHeartbeatRequest);
    }

    /**
     * Deserializes a {@code RegistrationConfirmedResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegistrationConfirmationResponse<C> getRegistrationConfirmedResponse(int messageId, TlvReader parameterReader) throws IOException {
        RegistrationConfirmationResponse<C> response = null;

        // deserializes the node address
        Tlv nodeAddressTlv = parameterReader.readNextTlv();
        ByteSerializableInteger nodeAddress = getInteger(nodeAddressTlv);
        response = new RegistrationConfirmationResponse<>(messageId, nodeAddress.getInteger());

        return response;
    }

    /**
     * Deserializes a {@code RegistrationDeclinedError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private RegistrationDeclinedError<C> getRegistrationDeclinedError(int messageId, TlvReader parameterReader) throws IOException {
        RegistrationDeclinedError<C> response = null;

        // deserializes the description
        Tlv descriptionTlv = parameterReader.readNextTlv();
        ByteSerializableString description = getString(descriptionTlv);

        response = new RegistrationDeclinedError<>(messageId, description.toString());

        return response;
    }

    /**
     * Deserializes a {@code HeartbeatRequest}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized request
     */
    private HeartbeatRequest<C> getHeartbeatRequest(int messageId, TlvReader parameterReader) {
        return new HeartbeatRequest<>(messageId);
    }
}
