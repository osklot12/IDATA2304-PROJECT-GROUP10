package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A response to a successful {@code FieldNodeActivateActuatorRequest}, indicating that the actuator was set to the
 * desired state.
 */
public class ActuatorStateSetServerResponse extends StandardProcessingResponseMessage<ServerContext> {
    /**
     * Creates a new ActuatorStateSetServerResponse.
     */
    public ActuatorStateSetServerResponse() {
        super(NofspSerializationConstants.ACTUATOR_STATE_SET_CODE);
    }

    /**
     * Creates a new ActuatorStateSetServerResponse.
     *
     * @param id the message id
     */
    public ActuatorStateSetServerResponse(int id) {
        this();

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ServerContext context) {
        // no further handling required
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this);
    }

    @Override
    public String toString() {
        return "state was successfully set for the actuator";
    }
}
