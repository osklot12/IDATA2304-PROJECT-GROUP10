package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.exception.SubscriptionException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.UnsubscribedFromFieldNodeResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.SubscriptionError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A request sent from a control panel to the central server, requesting to be unsubscribed from a field node.
 */
public class UnsubscribeFromFieldNodeRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final int fieldNodeAddress;

    /**
     * Creates a new UnsubscribeFromFieldNodeRequest.
     *
     * @param fieldNodeAddress the address of field node to unsubscribe from
     */
    public UnsubscribeFromFieldNodeRequest(int fieldNodeAddress) {
        super(NofspSerializationConstants.UNSUBSCRIBE_FROM_FIELD_NODE_COMMAND);

        this.fieldNodeAddress = fieldNodeAddress;
    }

    /**
     * Creates a new UnsubscribeFromFieldNodeRequest.
     *
     * @param id the message id
     * @param fieldNodeAddress the address of field node to unsubscribe from
     */
    public UnsubscribeFromFieldNodeRequest(int id, int fieldNodeAddress) {
        this(fieldNodeAddress);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        if (context.isClientRegistered()) {
            try {
                context.unsubscribeFromFieldNode(fieldNodeAddress);
                response = new UnsubscribedFromFieldNodeResponse(fieldNodeAddress);
            } catch (SubscriptionException e) {
                response = new SubscriptionError<>(e.getMessage());
            }
        } else {
            response = new AuthenticationFailedError<>();
        }

        return response;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, new ByteSerializableInteger(fieldNodeAddress));
    }

    @Override
    public String toString() {
        return "requesting to unsubscribe from field node with id " + fieldNodeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof UnsubscribeFromFieldNodeRequest u)) {
            return false;
        }

        return super.equals(u) && fieldNodeAddress == u.fieldNodeAddress;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodeAddress;

        return result;
    }
}
