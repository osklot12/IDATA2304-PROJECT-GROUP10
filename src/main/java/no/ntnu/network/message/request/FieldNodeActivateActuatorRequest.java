package no.ntnu.network.message.request;

import no.ntnu.exception.ActuatorInteractionFailedException;
import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.response.ActuatorStateSetServerResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.DeviceInteractionFailedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A request sent from the central server to a field node, requesting it to change the state of an actuator.
 */
public class FieldNodeActivateActuatorRequest extends StandardProcessingRequestMessage<FieldNodeContext> {
    private final int actuatorAddress;
    private final int newState;

    /**
     * Creates a new FieldNodeActivateActuatorRequest.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    public FieldNodeActivateActuatorRequest(int actuatorAddress, int newState) {
        super(NofspSerializationConstants.ACTUATOR_NOTIFICATION_COMMAND);

        this.actuatorAddress = actuatorAddress;
        this.newState = newState;
    }

    /**
     * Creates a new FieldNodeActivateActuatorRequest.
     *
     * @param id the message id
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    public FieldNodeActivateActuatorRequest(int id, int actuatorAddress, int newState) {
        this(actuatorAddress, newState);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(FieldNodeContext context) {
        ResponseMessage response = null;

        try {
            context.setActuatorState(actuatorAddress, newState);
            response = new ActuatorStateSetServerResponse();
        } catch (ActuatorInteractionFailedException e) {
            // create error message if state cannot be set
            response = new DeviceInteractionFailedError(e.getMessage());
        }

        return response;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, new ByteSerializableInteger(actuatorAddress),
                new ByteSerializableInteger(newState));
    }

    @Override
    public String toString() {
        return "requesting to set the state of actuator " + actuatorAddress + " to state " + newState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FieldNodeActivateActuatorRequest f)) {
            return false;
        }

        return super.equals(f) && actuatorAddress == f.actuatorAddress
                && newState == f.newState;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + actuatorAddress;
        result = result * 31 + newState;

        return result;
    }
}
