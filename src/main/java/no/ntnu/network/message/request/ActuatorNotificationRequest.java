package no.ntnu.network.message.request;

import no.ntnu.exception.NoSuchAddressException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.ServerFnsmUpdatedResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.ServerFnsmUpdateRejectedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent from a field node to the central server, notifying it about a change of state for an actuator and
 * requesting it to update its FNSM accordingly.
 */
public class ActuatorNotificationRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final int actuatorAddress;
    private final int newState;

    /**
     * Creates a new ActuatorNotificationRequest.
     *
     * @param actuatorAddress the address of the changed actuator
     * @param newState the new state for the actuator
     */
    public ActuatorNotificationRequest(int actuatorAddress, int newState) {
        super(NofspSerializationConstants.ACTUATOR_NOTIFICATION_COMMAND);

        this.actuatorAddress = actuatorAddress;
        this.newState = newState;
    }

    /**
     * Creates a new ActuatorNotificationRequest.
     *
     * @param id the message id
     * @param actuatorAddress the address of the changed actuator
     * @param newState the new state for the actuator
     */
    public ActuatorNotificationRequest(int id, int actuatorAddress, int newState) {
        this(actuatorAddress, newState);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        if (context.isClientRegistered()) {
            try {
                context.updateLocalActuatorState(actuatorAddress, newState);
                response = new ServerFnsmUpdatedResponse();
            } catch (NoSuchAddressException e) {
                response = new ServerFnsmUpdateRejectedError(e.getMessage());
            }
        } else {
            response = new AuthenticationFailedError<>();
        }

        return response;
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this, new ByteSerializableInteger(actuatorAddress),
                new ByteSerializableInteger(newState));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ActuatorNotificationRequest a)) {
            return false;
        }

        return super.equals(a) && actuatorAddress == a.actuatorAddress && newState == a.newState;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + actuatorAddress;
        result = result * 31 + newState;

        return result;
    }

    @Override
    public String toString() {
        return "requesting to update state for actuator " + actuatorAddress;
    }
}
