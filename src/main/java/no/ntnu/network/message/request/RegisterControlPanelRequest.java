package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.clientproxy.ControlPanelClientProxy;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Set;

/**
 * A request to register a {@code ControlPanel} at the central server.
 */
public class RegisterControlPanelRequest extends RequestMessage implements Message<ServerContext> {
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

    /**
     * Returns the compatibility list, in a serializable format.
     *
     * @return the compatibility list
     */
    public ByteSerializableSet<ByteSerializableString> getSerializableCompatibilityList() {
        return makeSetSerializable(compatibilityList);
    }

    /**
     * Creates a {@code ByteSerializableSet} out of a regular {@code Set} of DeviceClass constants.
     *
     * @param compatibilityList the compatibility list to convert
     * @return serializable compatibility list
     */
    private ByteSerializableSet<ByteSerializableString> makeSetSerializable(Set<DeviceClass> compatibilityList) {
        ByteSerializableSet<ByteSerializableString> serializableList = new ByteSerializableSet<>();

        compatibilityList.forEach(
                item -> serializableList.add(new ByteSerializableString(item.toString()))
        );

        return serializableList;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, getSerializableCompatibilityList());
    }

    @Override
    public void process(ServerContext context) throws IOException {
        context.logReceivingRequest(this);

        ResponseMessage response = null;
        try {
            int clientAddress = context.registerControlPanel(compatibilityList);
            response = new RegistrationConfirmationResponse<>(clientAddress);
        } catch (ClientRegistrationException e) {
            response = new RegistrationDeclinedError<>(e.getMessage());
        }
        response.setId(getId().getInteger());

        context.respond(response);
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
