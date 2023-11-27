package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

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
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this);
    }
}
