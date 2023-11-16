package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A response to a heartbeat, indicating that the client is still connected.
 */
public class HeartbeatResponse extends ResponseMessage implements Message<ServerContext> {
    /**
     * Creates a new HeartbeatResponse.
     */
    public HeartbeatResponse() {
        super(NofspSerializationConstants.HEART_BEAT_RESPONSE);
    }

    /**
     * Creates a new HeartbeatResponse.
     *
     * @param id the message id
     */
    public HeartbeatResponse(int id) {
        this();

        setId(id);
    }

    @Override
    public void process(ServerContext context) throws IOException {
        context.logReceivingResponse(this);
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitHeartbeatResponse(this);
    }

    @Override
    public String toString() {
        return "client still alive";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof HeartbeatResponse h)) {
            return false;
        }

        return super.equals(h);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
