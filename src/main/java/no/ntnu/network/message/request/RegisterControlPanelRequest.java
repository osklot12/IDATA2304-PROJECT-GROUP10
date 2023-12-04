package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Set;

/**
 * A request to register a {@code ControlPanel} at the central server.
 */
public class RegisterControlPanelRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final Set<DeviceClass> compatibilityList;
    private final int dataSinkPortNumber;

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param compatibilityList  the compatibility list for the control panel
     * @param dataSinkPortNumber the port number of the sensor data sink service
     */
    public RegisterControlPanelRequest(Set<DeviceClass> compatibilityList, int dataSinkPortNumber) {
        super(NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND);
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot create RegisterControlPanelRequest, because compatibility list is null");
        }

        this.compatibilityList = compatibilityList;
        this.dataSinkPortNumber = dataSinkPortNumber;
    }

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param messageId         the message id
     * @param compatibilityList the compatibility list for the control panel
     */
    public RegisterControlPanelRequest(int messageId, Set<DeviceClass> compatibilityList, int dataSinkPortNumber) {
        this(compatibilityList, dataSinkPortNumber);
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
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this,
                DataTypeConverter.getSerializableCompatibilityList(compatibilityList),
                new ByteSerializableInteger(dataSinkPortNumber));
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        try {
            int clientAddress = context.registerControlPanelClient(compatibilityList, dataSinkPortNumber);
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
