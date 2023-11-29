package no.ntnu.network.message.deserialize.component;

import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A class that provides a lookup mechanism for field node devices.
 * The DeviceLookupTable is used to get the device class for a given set of addresses.
 */
public interface DeviceLookupTable {
    /**
     * Returns the device class for a given device on a given client.
     *
     * @param clientAddress the address of the client owning the device
     * @param deviceAddress the address of the device itself
     * @return the device class for the device, null if no such device is found
     */
    DeviceClass lookup(int clientAddress, int deviceAddress);
}
