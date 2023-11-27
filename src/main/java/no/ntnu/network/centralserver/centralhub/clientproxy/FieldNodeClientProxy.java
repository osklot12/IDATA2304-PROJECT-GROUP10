package no.ntnu.network.centralserver.centralhub.clientproxy;

import no.ntnu.exception.AddressNotAvailableException;
import no.ntnu.exception.NoSuchActuatorException;
import no.ntnu.exception.NoSuchDeviceException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.CommunicationAgent;

import java.util.Map;

/**
 * A proxy for a field node client, used for storing information about the field node and interacting with it.
 */
public class FieldNodeClientProxy extends ClientProxy {
    private final Map<Integer, DeviceClass> fnst;
    private final Map<Integer, Integer> fnsm;
    private final String name;

    /**
     * Creates a new FieldNodeClientProxy.
     *
     * @param agent the client handler
     */
    public FieldNodeClientProxy(CommunicationAgent agent, Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        super(agent);

        this.fnst = fnst;
        this.fnsm = fnsm;
        this.name = name;
    }

    /**
     * Adds a device to the field node client proxy.
     *
     * @param address the address of the device
     * @param deviceClass the class of device
     * @throws IllegalArgumentException thrown if address is negative
     * @throws AddressNotAvailableException thrown if address is not available
     */
    public void addDevice(int address, DeviceClass deviceClass) throws IllegalArgumentException, AddressNotAvailableException {
        if (address < 0) {
            throw new IllegalArgumentException("Cannot register device, because address must be a non-negative integer.");
        }

        if (fnst.containsKey(address)) {
            throw new AddressNotAvailableException("Cannot register device, because address " + address +
                    " is not available.");
        }

        fnst.put(address, deviceClass);
    }

    /**
     * Removes a device from the field node client proxy.
     *
     * @param address the address of the device
     * @throws IllegalArgumentException thrown if address is negative
     * @throws NoSuchDeviceException thrown if no device with the given address exists
     */
    public void removeDevice(int address) throws IllegalArgumentException, NoSuchDeviceException {
        if (address < 0) {
            throw new IllegalArgumentException("Cannot register device, because address must be a non-negative integer.");
        }

        if (!(fnst.containsKey(address))) {
            throw new NoSuchDeviceException("Cannot remove device, because no device with address " + address + " exists.");
        }

        fnst.remove(address);
    }

    /**
     * Sets the state for a given actuator.
     * This notifies all listeners for this type of event.
     *
     * @param actuatorAddress the address of the actuator
     * @param state state to set
     * @throws NoSuchActuatorException thrown if no actuator with the given address exists
     */
    public void setActuatorState(int actuatorAddress, int state) throws NoSuchActuatorException {
        if (!(fnsm.containsKey(actuatorAddress))) {
            throw new NoSuchActuatorException();
        }

        fnsm.replace(actuatorAddress, state);
    }

    /**
     * Returns the field node system table.
     *
     * @return field node system table
     */
    public Map<Integer, DeviceClass> getFNST() {
        return fnst;
    }

    /**
     * Returns the field node status map.
     *
     * @return field node status map
     */
    public Map<Integer, Integer> getFNSM() {
        return fnsm;
    }

    /**
     * Returns the name for the field node client proxy.
     *
     * @return name of field node client proxy
     */
    public String getName() {
        return name;
    }
}