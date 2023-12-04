package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a {@code DisconnectRequest}, indicating the client is allowed to disconnect.
 * @param <C>
 */
public class DisconnectionAllowedResponse<C extends ClientContext> extends StandardProcessingResponseMessage<C> {
    /**
     * Creates a new DisconnectionAllowedResponse.
     */
    public DisconnectionAllowedResponse() {
        super(NofspSerializationConstants.DISCONNECTION_ALLOWED_CODE);
    }

    /**
     * Creates a new DisconnectionAllowedResponse.
     *
     * @param id the message id
     */
    public DisconnectionAllowedResponse(int id) {
        this();
        setId(id);
    }

    @Override
    protected void handleResponseProcessing(C context) {
        context.closeConnection();
    }

    @Override
    public String toString() {
        return "disconnection from server allowed";
    }
}
