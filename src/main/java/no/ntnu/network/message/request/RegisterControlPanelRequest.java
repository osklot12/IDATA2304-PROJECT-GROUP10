package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.util.Arrays;
import java.util.List;

/**
 * A request to register a {@code Control Panel} at the central server.
 */
public class RegisterControlPanelRequest implements Request {
    private static final String COMMAND = "REGCP";
    private final List<DeviceClass> compatibilityList;

    /**
     * Creates a new RegisterControlPanelRequest.
     *
     * @param compatibilityList the compatibility list for the control panel
     */
    public RegisterControlPanelRequest(List<DeviceClass> compatibilityList) {
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot create RegisterControlPanelRequest, because compatibility list is null");
        }

        this.compatibilityList = compatibilityList;
    }

    /**
     * Returns the compatibility list.
     *
     * @return the compatibility list
     */
    public List<DeviceClass> getCompatibilityList() {
        return compatibilityList;
    }

    /**
     * Returns a serializable compatibility list.
     *
     * @return serializable compatibility list
     */
    public ByteSerializableList<ByteSerializableString> getSerializableCompatibilityList() {
        return makeListSerializable(compatibilityList);
    }

    private ByteSerializableList<ByteSerializableString> makeListSerializable(List<DeviceClass> compatibilityList) {
        ByteSerializableList<ByteSerializableString> serializableList = new ByteSerializableList<>();

        compatibilityList.forEach(
                item -> serializableList.add(new ByteSerializableString(item.toString()))
        );

        return serializableList;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) {
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

        return r.getCommand().equals(COMMAND) && Arrays.equals(compatibilityList.toArray(), r.getCompatibilityList().toArray());
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + compatibilityList.hashCode();

        return result;
    }
}
