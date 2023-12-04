package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a {@code ServerFnsmNotificationRequest}, indicating that the virtual actuator has been updated
 * accordingly.
 */
public class VirtualActuatorUpdatedResponse extends StandardProcessingResponseMessage<ServerContext> {
    /**
     * Creates a new VirtualActuatorUpdatedResponse.
     */
    public VirtualActuatorUpdatedResponse() {
        super(NofspSerializationConstants.VIRTUAL_ACTUATOR_UPDATED_CODE);
    }

    /**
     * Creates a new VirtualActuatorUpdatedResponse.
     *
     * @param id the message id
     */
    public VirtualActuatorUpdatedResponse(int id) {
        this();

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ServerContext context) {
        // no processing required
    }

    @Override
    public String toString() {
        return "virtual actuator has changed state successfully";
    }
}
