package no.ntnu.fieldnode.device;

/**
 * A device connected to a field node.
 */
public interface Device {
    /**
     * Returns the class of the device.
     *
     * @return class of device
     */
    DeviceClass getDeviceClass();
}
