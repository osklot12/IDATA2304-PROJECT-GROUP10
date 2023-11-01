package no.ntnu.controlpanel.virtual;

import javafx.beans.Observable;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A representation of a device for a field node, used for storing data about the device it represents.
 */
public abstract class VirtualDevice {
    private final DeviceClass deviceClass;

    /**
     * Constructor for the VirtualDevice class.
     *
     * @param deviceClass the class for the device
     */
    public VirtualDevice(DeviceClass deviceClass) {
        if (deviceClass == null) {
            throw new IllegalArgumentException("Cannot create VirtualDevice, because the device class is not defined.");
        }

        this.deviceClass = deviceClass;
    }

    /**
     * Returns the device class.
     *
     * @return device class
     */
    public DeviceClass getDeviceClass() {
        return deviceClass;
    }
}
