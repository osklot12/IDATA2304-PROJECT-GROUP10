package no.ntnu.network.message.request;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.util.Set;

/**
 * A request to register a {@code Control Panel} at the central server.
 */
public class RegisterControlPanelRequest extends RequestMessage {
    private final ByteSerializableSet<ByteSerializableString> compatibilityList;

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param messageId the message ID
     * @param compatibilityList the compatibility list for the control panel
     */
    public RegisterControlPanelRequest(int messageId, Set<DeviceClass> compatibilityList) {
        super(messageId, NofspSerializationConstants.REGISTER_CONTROL_PANEL_COMMAND);
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot create RegisterControlPanelRequest, because compatibility list is null");
        }

        this.compatibilityList = makeSetSerializable(compatibilityList);
    }

    /**
     * Returns the compatibility list.
     *
     * @return the compatibility list
     */
    public ByteSerializableSet<ByteSerializableString> getCompatibilityList() {
        return compatibilityList;
    }

    private ByteSerializableSet<ByteSerializableString> makeSetSerializable(Set<DeviceClass> compatibilityList) {
        ByteSerializableSet<ByteSerializableString> serializableList = new ByteSerializableSet<>();

        compatibilityList.forEach(
                item -> serializableList.add(new ByteSerializableString(item.toString()))
        );

        return serializableList;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRegisterControlPanelRequest(this);
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
        int result = 17;

        result = result * 31 + compatibilityList.hashCode();

        return result;
    }
}
