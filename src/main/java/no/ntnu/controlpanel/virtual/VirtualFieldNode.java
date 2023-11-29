package no.ntnu.controlpanel.virtual;

import no.ntnu.broker.VirtualFieldNodeBroker;
import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.tools.logger.Logger;

import java.util.*;

/**
 * A representation of a field node, used for storing data for the field node it represents.
 * Unlike the FieldNode, the VirtualFieldNode does not contain any proper logic for the node.
 */
public class VirtualFieldNode implements VirtualSDUSensorListener, VirtualActuatorListener {
    private static final int VIRTUAL_SENSOR_BUFFER_SIZE = 10;
    private final Map<Integer, VirtualDevice> devices;
    private final VirtualFieldNodeBroker fieldNodeBroker;
    private final String name;
    private final Map<DeviceClass, VirtualDeviceCreator> virtualDeviceCreatorMap;

    /**
     * A functional interface defining virtual device creating methods.
     */
    @FunctionalInterface
    private interface VirtualDeviceCreator {
        VirtualDevice getVirtualDevice(DeviceClass deviceClass);
    }

    /**
     * Creates a new VirtualFieldNode.
     *
     * @param name the name of the field node
     */
    public VirtualFieldNode(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot create VirtualFieldNode, because name is null.");
        }

        devices = new HashMap<>();
        this.fieldNodeBroker = new VirtualFieldNodeBroker();
        this.name = name;
        this.virtualDeviceCreatorMap = new EnumMap<>(DeviceClass.class);
        initializeVirtualDeviceCreatorMap();
    }

    /**
     * Creates a new VirtualFieldNode.
     */
    public VirtualFieldNode() {
        this("field node");
    }

    /**
     * Initializes the mapping of device classes to virtual device creating methods.
     */
    private void initializeVirtualDeviceCreatorMap() {
        virtualDeviceCreatorMap.put(DeviceClass.S1, this::getVirtualSDUSensor);
        virtualDeviceCreatorMap.put(DeviceClass.S2, this::getVirtualSDUSensor);
        virtualDeviceCreatorMap.put(DeviceClass.S3, this::getVirtualSDUSensor);

        virtualDeviceCreatorMap.put(DeviceClass.A1, this::getVirtualStandardActuator);
        virtualDeviceCreatorMap.put(DeviceClass.A2, this::getVirtualStandardActuator);
        virtualDeviceCreatorMap.put(DeviceClass.A3, this::getVirtualStandardActuator);
    }

    /**
     * Creates a virtual Sdu sensor.
     *
     * @param deviceClass the device class of the sensor
     * @return the virtual sdu sensor
     */
    private VirtualSDUSensor getVirtualSDUSensor(DeviceClass deviceClass) {
        return new VirtualSDUSensor(deviceClass, VIRTUAL_SENSOR_BUFFER_SIZE);
    }

    /**
     * Creates a new virtual standard actuator.
     *
     * @param deviceClass the device class of the actuator
     * @return the virtual standard actuator
     */
    private VirtualStandardActuator getVirtualStandardActuator(DeviceClass deviceClass) {
        return new VirtualStandardActuator(deviceClass, 0);
    }

    /**
     * Returns a virtual address with the given address.
     *
     * @param deviceAddress the address of the virtual device
     * @return the associated virtual device, null if no match is found
     */
    public VirtualDevice getVirtualDevice(int deviceAddress) {
        return devices.get(deviceAddress);
    }

    /**
     * Creates and adds virtual devices based on an FNST.
     *
     * @param fnst the fnst to add from
     */
    public void addVirtualDevicesFromFnst(Map<Integer, DeviceClass> fnst) {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot add virtual devices from fnst, because fnst is null.");
        }

        fnst.forEach((address, deviceClass) -> {
            VirtualDeviceCreator deviceCreator = virtualDeviceCreatorMap.get(deviceClass);
            if (deviceCreator != null) {
                VirtualDevice virtualDevice = deviceCreator.getVirtualDevice(deviceClass);
                addVirtualDevice(address, virtualDevice);
            }
        });
    }

    /**
     * Sets the state for each virtual actuator of the FNSM.
     *
     * @param fnsm the fnsm to use for setting states
     */
    public void setVirtualActuatorStatesFromFnsm(Map<Integer, Integer> fnsm) {
        if (fnsm == null) {
            throw new NoSuchVirtualDeviceException("Cannot set virtual actuator states, because fnsm is null.");
        }

        fnsm.forEach((address, state) -> setVirtualStandardActuatorState(address, state, false));
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
