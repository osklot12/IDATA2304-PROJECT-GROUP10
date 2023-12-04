package no.ntnu.network.message.request;

import no.ntnu.exception.SubscriptionException;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.SubscribedToFieldNodeResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.SubscriptionError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent from a control panel to the central server, asking to subscribe to a specific field node.
 * This subscription makes it possible for the control panel to receive sensor data, and manipulate the actuators
 * for the field node.
 */
public class SubscribeToFieldNodeRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final int fieldNodeAddress;

    /**
     * Creates a new SubscribeToFieldNodeRequest.
     *
     * @param fieldNodeAddress the node address of the field node to subscribe to
     */
    public SubscribeToFieldNodeRequest(int fieldNodeAddress) {
        super(NofspSerializationConstants.SUBSCRIBE_TO_FIELD_NODE_COMMAND);

        this.fieldNodeAddress = fieldNodeAddress;
    }

    /**
     * Creates a new SubscribeToFieldNodeRequest.
     *
     * @param id               the message id
     * @param fieldNodeAddress the node address of the field node to subscribe to
     */
    public SubscribeToFieldNodeRequest(int id, int fieldNodeAddress) {
        this(fieldNodeAddress);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        if (context.isClientRegistered()) {
            try {
                FieldNodeClientProxy fieldNodeProxy = context.subscribeToFieldNode(fieldNodeAddress);
                response = new SubscribedToFieldNodeResponse(fieldNodeAddress, fieldNodeProxy.getFNST(), fieldNodeProxy.getFNSM(), fieldNodeProxy.getName());
            } catch (SubscriptionException e) {
                response = new SubscriptionError<>(e.getMessage());
            }
        } else {
            response = new AuthenticationFailedError<>();
        }

        return response;
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this, new ByteSerializableInteger(fieldNodeAddress));
    }

    @Override
    public String toString() {
        return "requesting to subscribe to field node with id " + fieldNodeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SubscribeToFieldNodeRequest s)) {
            return false;
        }

        return super.equals(s) && fieldNodeAddress == s.fieldNodeAddress;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodeAddress;

        return result;
    }
}
