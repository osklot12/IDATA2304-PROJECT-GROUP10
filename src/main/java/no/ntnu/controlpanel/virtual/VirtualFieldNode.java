package no.ntnu.controlpanel.virtual;

import no.ntnu.controlpanel.virtual.actuator.ActuatorRelationAddress;
import no.ntnu.controlpanel.virtual.actuator.AddressedVActuatorListener;
import no.ntnu.controlpanel.virtual.actuator.VirtualStandardActuator;
import no.ntnu.controlpanel.virtual.sensor.VirtualSDUSensor;
import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.*;

/**
 * A representation of a field node, used for storing data about the field node it represents.
 */
public class VirtualFieldNode implements AddressedVActuatorListener {
    private static final int VIRTUAL_SENSOR_BUFFER_SIZE = 20;
    private final Map<Integer, VirtualSDUSensor> virtualSduSensors;
    private final Map<Integer, VirtualStandardActuator> virtualStandardActuators;
    private final String name;
    private final Map<DeviceClass, VirtualDeviceCreator> virtualDeviceCreatorMap;
    private final Map<Integer, VirtualFieldNodeListener> listeners;

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

        this.virtualSduSensors = new HashMap<>();
        this.virtualStandardActuators = new HashMap<>();
        this.name = name;
        this.virtualDeviceCreatorMap = new EnumMap<>(DeviceClass.class);
        this.listeners = new HashMap<>();
        initializeVirtualDeviceCreatorMap();
    }

    /**
     * Initializes the mapping of device classes to virtual device creating methods.
     */
    private void initializeVirtualDeviceCreatorMap() {
        virtualDeviceCreatorMap.put(DeviceClass.S1, this::createVirtualSDUSensor);
        virtualDeviceCreatorMap.put(DeviceClass.S2, this::createVirtualSDUSensor);
        virtualDeviceCreatorMap.put(DeviceClass.S3, this::createVirtualSDUSensor);

        virtualDeviceCreatorMap.put(DeviceClass.A1, this::createVirtualStandardActuator);
        virtualDeviceCreatorMap.put(DeviceClass.A2, this::createVirtualStandardActuator);
        virtualDeviceCreatorMap.put(DeviceClass.A3, this::createVirtualStandardActuator);
    }

    /**
     * Adds a listeners to listen for events for the virtual field node.
     * Each listener is associated with an address, and this address is used when notifying the listener
     * about the event.
     *
     * @param address the address of the relation
     * @param listener the listener to add
     */
    public void addListener(int address, VirtualFieldNodeListener listener) {
        listeners.put(address, listener);
    }

    /**
     * Returns the name of the virtual field node.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Creates a virtual Sdu sensor.
     *
     * @param deviceClass the device class of the sensor
     * @return the virtual sdu sensor
     */
    private VirtualSDUSensor createVirtualSDUSensor(DeviceClass deviceClass) {
        return new VirtualSDUSensor(deviceClass, VIRTUAL_SENSOR_BUFFER_SIZE);
    }

    /**
     * Creates a new virtual standard actuator.
     *
     * @param deviceClass the device class of the actuator
     * @return the virtual standard actuator
     */
    private VirtualStandardActuator createVirtualStandardActuator(DeviceClass deviceClass) {
        return new VirtualStandardActuator(deviceClass, 0);
    }

    /**
     * Returns a virtual address with the given address.
     *
     * @param deviceAddress the address of the virtual device
     * @return the associated virtual device, null if no match is found
     */
    public VirtualDevice getVirtualDevice(int deviceAddress) {
        return getVirtualDevices().get(deviceAddress);
    }

    /**
     * Returns all virtual SDU sensors.
     *
     * @return all virtual sdu sensors
     */
    public Map<Integer, VirtualSDUSensor> getVirtualSDUSensors() {
        return virtualSduSensors;
    }

    /**
     * Returns all virtual standard actuators.
     *
     * @return all virtual standard actuators
     */
    public Map<Integer, VirtualStandardActuator> getVirtualStandardActuators() {
        return virtualStandardActuators;
    }

    /**
     * Returns the virtual standard actuator with the given address.
     *
     * @param address the address of the virtual standard actuator
     * @return the virtual standard actuator, null if no such actuator exists
     */
    public VirtualStandardActuator getVirtualStandardActuator(int address) {
        return virtualStandardActuators.get(address);
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
                if (virtualDevice instanceof VirtualSDUSensor sduSensor) {
                    addVirtualSduSensor(address, sduSensor);
                }

                if (virtualDevice instanceof VirtualStandardActuator standardActuator) {
                    addVirtualStandardActuator(address, standardActuator);
                }
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
     * Adds a virtual SDU sensor to the virtual field node.
     *
     * @param address the address of the virtual sdu sensor
     * @param virtualSDUSensor the virtual sdu sensor to add
     */
    public void addVirtualSduSensor(int address, VirtualSDUSensor virtualSDUSensor) {
        virtualSduSensors.put(address, virtualSDUSensor);
    }

    /**
     * Adds a virtual standard actuator to the virtual field node.
     *
     * @param address the address of the virtual standard actuator
     * @param virtualStandardActuator the virtual standard actuator to add
     */
    public void addVirtualStandardActuator(int address, VirtualStandardActuator virtualStandardActuator) {
        virtualStandardActuators.put(address, virtualStandardActuator);
        virtualStandardActuator.addListener(new ActuatorRelationAddress(address, this));
    }

    /**
     * Returns a map of all the virtual devices for the virtual field node.
     *
     * @return map of virtual devices
     */
    public Map<Integer, VirtualDevice> getVirtualDevices() {
        Map<Integer, VirtualDevice> result = new HashMap<>();

        result.putAll(getVirtualSDUSensors());
        result.putAll(getVirtualStandardActuators());

        return result;
    }

    /**
     * Adds SDU sensor data to a particular sdu sensor.
     *
     * @param sensorAddress the address of the sensor
     * @param data the sdu data to add
     * @throws NoSuchVirtualDeviceException thrown if no virtual sdu sensor exists for the given address
     */
    public void addSDUSensorData(int sensorAddress, double data) throws NoSuchVirtualDeviceException {
        VirtualSDUSensor sensor = virtualSduSensors.get(sensorAddress);
        if (sensor == null) {
            throw new NoSuchVirtualDeviceException("Cannot add sdu sensor data, because there is no virtual SDU" +
                    "sensor with the given address");
        }

        sensor.addSensorData(data);
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
        VirtualStandardActuator actuator = virtualStandardActuators.get(actuatorAddress);
        if (actuator == null) {
            throw new NoSuchVirtualDeviceException("Cannot set virtual actuator state, because there is no virtual" +
                    "standard actuator with the given address.");
        }

        actuator.setState(state, this);
    }

    @Override
    public void virtualActuatorStateChanged(int actuatorAddress) {
        listeners.forEach((address, listener) -> listener.virtualActuatorStateChanged(address, actuatorAddress));
    }
}
