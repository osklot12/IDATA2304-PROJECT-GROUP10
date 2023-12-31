package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a heartbeat, indicating that the client is still connected.
 */
public class HeartbeatResponse extends StandardProcessingResponseMessage<ServerContext> {
    /**
     * Creates a new HeartbeatResponse.
     */
    public HeartbeatResponse() {
        super(NofspSerializationConstants.HEART_BEAT_CODE);
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
    protected void handleResponseProcessing(ServerContext context) {
        // no further processing required
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
