package no.ntnu.network.message.request;

import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.VirtualActuatorUpdatedResponse;
import no.ntnu.network.message.response.error.NoSuchVirtualDeviceError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A request sent from the central server to a control panel, requesting it to update its state for a virtual
 * field node. The request is sent in the event of an FNSM being updated at the central server.
 */
public class ServerFnsmNotificationRequest extends StandardProcessingRequestMessage<ControlPanelContext> {
    private final int fieldNodeAddress;
    private final int actuatorAddress;
    private final int newState;

    /**
     * Creates a new ServerFnsmNotificationRequest.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    public ServerFnsmNotificationRequest(int fieldNodeAddress, int actuatorAddress, int newState) {
        super(NofspSerializationConstants.FNSM_NOTIFICATION_COMMAND);

        this.fieldNodeAddress = fieldNodeAddress;
        this.actuatorAddress = actuatorAddress;
        this.newState = newState;
    }

    /**
     * Creates a new ServerFnsmNotificationRequest.
     *
     * @param id the message id
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    public ServerFnsmNotificationRequest(int id, int fieldNodeAddress, int actuatorAddress, int newState) {
        this(fieldNodeAddress, actuatorAddress, newState);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ControlPanelContext context) {
        ResponseMessage response = null;

        try {
            context.setActuatorStatus(fieldNodeAddress, actuatorAddress, newState);
            response = new VirtualActuatorUpdatedResponse();
        } catch (NoSuchVirtualDeviceException e) {
            response = new NoSuchVirtualDeviceError(e.getMessage());
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
        return "requesting to set state of actuator " + actuatorAddress + " on field node " + fieldNodeAddress +
                " to " + newState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ServerFnsmNotificationRequest s)) {
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
