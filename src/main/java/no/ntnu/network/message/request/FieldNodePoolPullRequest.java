package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;

/**
 * A request sent from a control panel to the central server, requesting the field node pool.
 */
public class FieldNodePoolPullRequest extends RequestMessage implements Message<ServerContext> {
    /**
     * Creates a new FieldNodePoolPullRequest.
     */
    public FieldNodePoolPullRequest() {
        super(NofspSerializationConstants.FIELD_NODE_POOL_PULL);
    }

    /**
     * Creates a new FieldNodePoolPullRequest.
     *
     * @param id the message id
     */
    public FieldNodePoolPullRequest(int id) {
        super(NofspSerializationConstants.FIELD_NODE_POOL_PULL);

        setId(id);
    }

    @Override
    public void process(ServerContext context) throws IOException {
        context.logReceivingRequest(this);

        ResponseMessage response = null;
        if (context.isClientRegistered()) {

        } else {

        }

        context.respond(response);
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FieldNodePoolPullRequest f)) {
            return false;
        }

        return super.equals(f);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
