package no.ntnu.controlpanel.virtual;

import no.ntnu.broker.VirtualFieldNodeBroker;
import no.ntnu.exception.NoSuchVirtualDeviceException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A representation of a field node, used for storing data for the field node it represents.
 * Unlike the FieldNode, the VirtualFieldNode does not contain any proper logic for the node.
 */
public class VirtualFieldNode implements VirtualSDUSensorListener, VirtualActuatorListener {
    private final Map<Integer, VirtualDevice> devices;
    private final VirtualFieldNodeBroker fieldNodeBroker;

    /**
     * Creates a new VirtualFieldNode.
     */
    public VirtualFieldNode() {
        devices = new HashMap<>();
        this.fieldNodeBroker = new VirtualFieldNodeBroker();
    }

    /**
     * Adds a virtual device to the virtual field node.
     *
     * @param address the address for the virtual device
     * @param device the virtual device to add
     * @return true if successfully added
     */
    public boolean addVirtualDevice(int address, VirtualDevice device) {
        boolean success = false;

        if (!(devices.containsKey(address))) {
            if (connectVirtualDevice(device)) {
                devices.put(address, device);
                success = true;
            } else {
                disconnectVirtualDevice(device);
            }
        }

        return success;
    }

    /**
     * Returns a collection of all the virtual devices for the virtual field node.
     *
     * @return collection of virtual devices
     */
    public Collection<VirtualDevice> getVirtualDevices() {
        return devices.values();
    }

    public int getVirtualActuatorState(int actuatorAddress) {
        int state = -1;

        if (devices.containsKey(actuatorAddress) && devices.get(actuatorAddress) instanceof VirtualStandardActuator actuator) {
            state = actuator.getState();
        }

        return state;
    }

    private boolean connectVirtualDevice(VirtualDevice device) {
        boolean success = true;

        if (device instanceof VirtualSDUSensor virtualSDUSensor) {
            success = virtualSDUSensor.addListener(this);
        }

        if (device instanceof VirtualStandardActuator virtualStandardActuator) {
            success = virtualStandardActuator.addListener(this);
        }

        return success;
    }

    private void disconnectVirtualDevice(VirtualDevice device) {
        if (device instanceof VirtualSDUSensor virtualSDUSensor) {
            virtualSDUSensor.removeListener(this);
        }

        if (device instanceof VirtualStandardActuator virtualStandardActuator) {
            virtualStandardActuator.removeListener(this);
        }
    }

    private int getVirtualDeviceAddress(VirtualDevice virtualDevice) {
        int address = -1;

        Iterator<Map.Entry<Integer, VirtualDevice>> entryIterator = devices.entrySet().iterator();

        while(address == -1 && entryIterator.hasNext()) {
            Map.Entry<Integer, VirtualDevice> entry = entryIterator.next();

            if (virtualDevice.equals(entry.getValue())) {
                address = entry.getKey();
            }
        }

        return address;
    }

    /**
     * Adds SDU sensor data to a particular sdu sensor.
     *
     * @param sensorAddress the address of the sensor
     * @param data the sdu data to add
     * @throws NoSuchVirtualDeviceException thrown if no virtual sdu sensor exists for the given address
     */
    public void addSDUSensorData(int sensorAddress, double data) throws NoSuchVirtualDeviceException {
        if (!(devices.containsKey(sensorAddress)) || !(devices.get(sensorAddress) instanceof VirtualSDUSensor)) {
            throw new NoSuchVirtualDeviceException("Cannot add sdu sensor data, because there is no virtual SDU" +
                    "sensor with the given address");
        }

        ((VirtualSDUSensor) devices.get(sensorAddress)).addSensorData(data);
    }

    /**
     * Sets the state for a virtual standard actuator.
     *
     * @param actuatorAddress the address of the actuator
     * @param state the state to set
     * @param global true if change should be asserted globally, false if change is only locally
     * @throws NoSuchVirtualDeviceException thrown if no virtual standard actuator exists for the given address
     */
    public void setVirtualStandardActuatorState(int actuatorAddress, int state, boolean global) throws NoSuchVirtualDeviceException {
        if (!(devices.containsKey(actuatorAddress)) || !(devices.get(actuatorAddress) instanceof VirtualStandardActuator virtualActuator)) {
            throw new NoSuchVirtualDeviceException("Cannot set virtual actuator state, because there is no virtual" +
                    "standard actuator with the given address.");
        }

        virtualActuator.setState(state, global);
    }

    @Override
    public void virtualActuatorStateChanged(VirtualStandardActuator actuator, boolean global) {
        int actuatorAddress = getVirtualDeviceAddress(actuator);

        if (actuatorAddress != -1) {
            fieldNodeBroker.notifyActuatorStateChanged(this, actuatorAddress, global);
        }
    }

    @Override
    public void newSDUData(VirtualSDUSensor virtualSDUSensor) {
        fieldNodeBroker.notifyNewSDUData(virtualSDUSensor);
    }
}
