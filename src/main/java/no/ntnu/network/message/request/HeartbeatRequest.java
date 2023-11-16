package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent by the server to any client, to check if the client is still connected.
 *
 * @param <C> any client context
 */
public class HeartbeatRequest<C extends ClientContext> extends RequestMessage implements Message<C> {
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
    public void process(C context) throws IOException {
        context.logReceivingRequest(this);

    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitHeartbeatRequest(this);
    }

    @Override
    public String toString() {
        return "Heartbeat <3";
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
