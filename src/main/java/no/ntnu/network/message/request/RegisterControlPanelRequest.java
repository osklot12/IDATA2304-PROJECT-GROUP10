package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Set;

/**
 * A request to register a {@code ControlPanel} at the central server.
 */
public class RegisterControlPanelRequest extends RequestMessage<ServerContext> {
    private final Set<DeviceClass> compatibilityList;

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param compatibilityList the compatibility list for the control panel
     */
    public RegisterControlPanelRequest(Set<DeviceClass> compatibilityList) {
        super(NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND);
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot create RegisterControlPanelRequest, because compatibility list is null");
        }

        this.compatibilityList = compatibilityList;
    }

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param messageId the message id
     * @param compatibilityList the compatibility list for the control panel
     */
    public RegisterControlPanelRequest(int messageId, Set<DeviceClass> compatibilityList) {
        this(compatibilityList);
        setId(messageId);
    }

    /**
     * Returns the compatibility list.
     *
     * @return the compatibility list
     */
    public Set<DeviceClass> getCompatibilityList() {
        return compatibilityList;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, DataTypeConverter.getSerializableCompatibilityList(compatibilityList));
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        try {
            int clientAddress = context.registerControlPanel(compatibilityList);
            response = new RegistrationConfirmationResponse<>(clientAddress);
        } catch (ClientRegistrationException e) {
            response = new RegistrationDeclinedError<>(e.getMessage());
        }

        return response;
    }

    @Override
    public String toString() {
        return "requesting to register control panel";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RegisterControlPanelRequest r)) {
            return false;
        }

        return super.equals(r) && compatibilityList.equals(r.getCompatibilityList());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + compatibilityList.hashCode();

        return result;
    }
}
