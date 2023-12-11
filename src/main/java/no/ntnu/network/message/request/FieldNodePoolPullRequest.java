package no.ntnu.network.message.request;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.FieldNodePoolResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent from a control panel to the central server, requesting the field node pool.
 */
public class FieldNodePoolPullRequest extends StandardProcessingRequestMessage<ServerContext> {
    /**
     * Creates a new FieldNodePoolPullRequest.
     */
    public FieldNodePoolPullRequest() {
        super(NofspSerializationConstants.FIELD_NODE_POOL_PULL_COMMAND);
    }

    /**
     * Creates a new FieldNodePoolPullRequest.
     *
     * @param id the message id
     */
    public FieldNodePoolPullRequest(int id) {
        this();

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        return new FieldNodePoolResponse(context.getFieldNodePool());
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
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

    @Override
    public String toString() {
        return "requesting to pull field node pool";
    }
}
