package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.response.HeartbeatResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent by the server to any client, to check if the client is still connected.
 *
 * @param <C> any client context
 */
public class HeartbeatRequest<C extends ClientContext> extends RequestMessage<C> {
    /**
     * Creates a new HeartbeatRequest.
     */
    public HeartbeatRequest() {
        super(NofspSerializationConstants.HEART_BEAT);
    }

    /**
     * Creates a new HeartbeatRequest.
     *
     * @param id the message id
     */
    public HeartbeatRequest(int id) {
        this();

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(C context) {
        return new HeartbeatResponse();
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this);
    }

    @Override
    public String toString() {
        return "heartbeat";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof HeartbeatRequest<?> h)) {
            return false;
        }

        return super.equals(h);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
