package no.ntnu.network.message.request;

import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ActuatorStateSetControlPanelResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.DeviceInteractionFailedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent from a control panel to the central server, requesting it to activate an actuator for a given
 * field node.
 */
public class ServerActivateActuatorRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final int fieldNodeAddress;
    private final int actuatorAddress;
    private final int newState;

    /**
     * Creates a new ServerActivateActuatorRequest.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress  the address of the actuator
     * @param newState         the new state to set
     */
    public ServerActivateActuatorRequest(int fieldNodeAddress, int actuatorAddress, int newState) {
        super(NofspSerializationConstants.ACTIVATE_ACTUATOR_COMMAND);

        this.fieldNodeAddress = fieldNodeAddress;
        this.actuatorAddress = actuatorAddress;
        this.newState = newState;
    }

    /**
     * Creates a new ServerActivateActuatorRequest.
     *
     * @param id               the message id
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress  the address of the actuator
     * @param newState         the new state to set
     */
    public ServerActivateActuatorRequest(int id, int fieldNodeAddress, int actuatorAddress, int newState) {
        this(fieldNodeAddress, actuatorAddress, newState);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        if (context.isClientRegistered()) {
            try {
                context.requestActuatorActivationForFieldNode(fieldNodeAddress, actuatorAddress, newState);
                response = new ActuatorStateSetControlPanelResponse();
            } catch (IOException e) {
                response = new DeviceInteractionFailedError(e.getMessage());
            }
        } else {
            response = new AuthenticationFailedError<>();
        }

        return response;
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this, new ByteSerializableInteger(fieldNodeAddress),
                new ByteSerializableInteger(actuatorAddress), new ByteSerializableInteger(newState));
    }

    @Override
    public String toString() {
        return "requesting to set the state of actuator " + actuatorAddress + " for field node " + fieldNodeAddress +
                " to " + newState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ServerActivateActuatorRequest s)) {
            return false;
        }

        return super.equals(s) && fieldNodeAddress == s.fieldNodeAddress && actuatorAddress == s.actuatorAddress
                && newState == s.newState;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodeAddress;
        result = result * 31 + actuatorAddress;
        result = result * 31 + newState;

        return result;
    }
}
