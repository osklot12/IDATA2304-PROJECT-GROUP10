package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a successful {@code ServerActivateActuatorRequest}, indicating that the desired state was set
 * for the appropriate actuator.
 */
public class ActuatorStateSetControlPanelResponse extends StandardProcessingResponseMessage<ControlPanelContext> {
    /**
     * Creates a new ActuatorStateSetControlPanelResponse.
     */
    public ActuatorStateSetControlPanelResponse() {
        super(NofspSerializationConstants.ACTUATOR_STATE_SET_CODE);
    }

    /**
     * Creates a new ActuatorStateSetControlPanelResponse.
     *
     * @param id the message id
     */
    public ActuatorStateSetControlPanelResponse(int id) {
        this();

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ControlPanelContext context) {

    }

    @Override
    public String toString() {
        return "request to activate actuator has been sent to field node";
    }
}
