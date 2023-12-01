package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

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
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this);
    }

    @Override
    public String toString() {
        return "disconnection from server allowed";
    }
}
