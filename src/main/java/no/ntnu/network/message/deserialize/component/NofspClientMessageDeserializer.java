package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.response.DisconnectionAllowedResponse;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.error.SubscriptionError;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
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

        initializeCommonDeserializationMethods();
    }

    /**
     * Adds the implemented server message deserialization methods to the lookup tables.
     */
    private void initializeCommonDeserializationMethods() {
        // requests
        addRequestMessageDeserialization(NofspSerializationConstants.HEART_BEAT, this::getHeartbeatRequest);

        // responses
        addResponseMessageDeserialization(NofspSerializationConstants.NODE_REGISTRATION_CONFIRMED_CODE, this::getRegistrationConfirmedResponse);
        addResponseMessageDeserialization(NofspSerializationConstants.NODE_REGISTRATION_DECLINED_CODE, this::getRegistrationDeclinedError);
        addResponseMessageDeserialization(NofspSerializationConstants.SUBSCRIPTION_FAILED_CODE, this::getNoSuchNodeError);
        addResponseMessageDeserialization(NofspSerializationConstants.DISCONNECTION_ALLOWED_CODE, this::getDisconnectionAllowedResponse);
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
        int nodeAddress = getRegularInt(parameterReader.readNextTlv());

        response = new RegistrationConfirmationResponse<>(messageId, nodeAddress);

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
        String description = getRegularString(parameterReader.readNextTlv());

        response = new RegistrationDeclinedError<>(messageId, description);

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

    /**
     * Deserializes a {@code NoSuchNodeError}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized response
     * @throws IOException thrown if an I/O exception occurs
     */
    private SubscriptionError<C> getNoSuchNodeError(int messageId, TlvReader parameterReader) throws IOException {
        SubscriptionError<C> response = null;

        // deserializes the description
        String description = getRegularString(parameterReader.readNextTlv());

        response = new SubscriptionError<>(messageId, description);

        return response;
    }

    /**
     * Deserializes a {@code DisconnectionAllowedResponse}.
     *
     * @param messageId the message id
     * @param parameterReader a TlvReader holding the response parameters
     * @return the deserialized response
     */
    private DisconnectionAllowedResponse<C> getDisconnectionAllowedResponse(int messageId, TlvReader parameterReader) {
        return new DisconnectionAllowedResponse<>(messageId);
    }
}
