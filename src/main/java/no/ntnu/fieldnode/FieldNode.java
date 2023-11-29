package no.ntnu.fieldnode;

import no.ntnu.broker.FieldNodeEventBroker;
import no.ntnu.fieldnode.device.Device;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.fieldnode.device.actuator.Actuator;
import no.ntnu.fieldnode.device.actuator.ActuatorListener;
import no.ntnu.fieldnode.device.sensor.Sensor;
import no.ntnu.fieldnode.device.sensor.SduSensorListener;
import no.ntnu.environment.Environment;
import no.ntnu.exception.ActuatorInteractionFailedException;

import java.util.*;

/**
 * A class responsible for the logic of the field node in the network.
 * A field node is a subsystem in the network consisting of sensors and actuators.
 */
public class FieldNode implements SduSensorListener, ActuatorListener {
    private static final String STANDARD_NAME = "FieldNode";
    private Environment environment;
    private final Map<Integer, Device> devices;
    private final String name;
    private final FieldNodeEventBroker eventBroker;
    private double latestSensorData;

    /**
     * Creates a new FieldNode.
     *
     * @param environment the environment in which to place the field node
     * @param name the name of the field node
     */
    public FieldNode(Environment environment, String name) {
        if (environment == null) {
            throw new IllegalArgumentException("Cannot create FieldNode, because environment is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create FieldNode, because name is null.");
        }

        this.environment = environment;
        this.devices = new HashMap<>();
        this.name = name;
        this.eventBroker = new FieldNodeEventBroker();
    }

    /**
     * Creates a new FieldNode.
     *
     * @param environment the environment in which to place the field node
     */
    public FieldNode(Environment environment) {
        this(environment, STANDARD_NAME);
    }

    /**
     * Returns the environment in which the field node is placed.
     *
     * @return environment for field node
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment for the field node.
     *
     * @param environment the environment to set
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        setEnvironmentForAllDevices(environment);
    }

    /**
     * Adds a listener listening for the events of the field node.
     *
     * @param listener the listener to add
     */
    public void addListener(FieldNodeListener listener) {
        eventBroker.addSubscriber(listener);
    }

    /**
     * Returns the Field Node System Table (FNST) for the field node.
     * The FNST contains the device class and assigned address for every device connected to the field node.
     *
     * @return FNST for field node
     */
    public Map<Integer, DeviceClass> getFNST() {
        Map<Integer, DeviceClass> fnst = new HashMap<>();

        devices.forEach((key, value) -> fnst.put(key, value.getDeviceClass()));

        return fnst;
    }

    /**
     * Returns the Field Node Status Map (FNSM) for the field node.
     * The FNSM contains all the actuator addresses and their respective state.
     *
     * @return FNSM for field node
     */
    public Map<Integer, Integer> getFNSM() {
        Map<Integer, Integer> fnsm = new HashMap<>();

        getActuators().forEach((key, value) -> fnsm.put(key, value.getState()));

        return fnsm;
    }

    /**
     * Returns the name of the field node.
     *
     * @return the name of field node
     */
    public String getName() {
        return name;
    }

    private Map<Integer, Actuator> getActuators() {
        Map<Integer, Actuator> actuators = new HashMap<>();

        devices.forEach((key, value) -> {
            if (value instanceof Actuator actuator) {
                actuators.put(key, actuator);
            }
        });

        return actuators;
    }

    private Map<Integer, Sensor> getSensors() {
        Map<Integer, Sensor> sensors = new HashMap<>();

        devices.forEach((key, value) -> {
            if (value instanceof Sensor sensor) {
                sensors.put(key, sensor);
            }
        });

        return sensors;
    }

    private void setEnvironmentForAllDevices(Environment environment) {
        devices.values().forEach(device -> device.setEnvironment(environment));
    }

    /**
     * Adds a device to the field node, assigning it an address automatically.
     *
     * @param device device to add
     * @return the assigned address of the sensor, -1 on error
     */
    public int addDevice(Device device) {
        int address = -1;

        if (!(devices.containsValue(device))) {
            address = generateNewDeviceAddress();
            connectDevice(device, address);
            devices.put(address, device);
        }

        return address;
    }

    private void connectDevice(Device device, int address) {
        device.setEnvironment(environment);

        if (device instanceof Sensor sensor) {
            sensor.addListener(this, address);
            sensor.start();
        }

        if (device instanceof Actuator actuator) {
            actuator.addListener(this, address);
        }
    }

    private int generateNewDeviceAddress() {
        int currentCheck = 0;

        while (devices.containsKey(currentCheck)) {
            currentCheck++;
        }

        return currentCheck;
    }

    /**
     * Sets the state for an actuator connected to the field node.
     *
     * @param actuatorAddress the address of the actuator in which to change the state
     * @param state the new state to set
     * @throws ActuatorInteractionFailedException throws an exception if the state could not be set
     */
    public void setActuatorState(int actuatorAddress, int state) throws ActuatorInteractionFailedException {
        if (!getActuators().containsKey(actuatorAddress)) {
            throw new ActuatorInteractionFailedException("Cannot change state of actuator, because an actuator " +
                    "with the given address does not exist.");
        }

        try {
            getActuators().get(actuatorAddress).setState(state);
        } catch (Exception e) {
            throw new ActuatorInteractionFailedException(e.getMessage());
        }
    }

    /**
     * Returns the state for a given actuator.
     *
     * @param actuatorAddress the address of the actuator in which to get the state
     * @return the state of the actuator
     * @throws ActuatorInteractionFailedException throws an exception if the actuator address is not valid
     */
    public int getActuatorState(int actuatorAddress) throws ActuatorInteractionFailedException {
        if (!getActuators().containsKey(actuatorAddress)) {
            throw new ActuatorInteractionFailedException("Cannot get the state of actuator, because an actuator with" +
                    " the given address does not exist.");
        }

        return getActuators().get(actuatorAddress).getState();
    }

    /**
     * Returns the latest sdu sensor data.
     *
     * @return latest sdu sensor data
     */
    public double getLatestSensorData() {
        return latestSensorData;
    }

    @Override
    public void sduDataCaptured(int sensorAddress, double data) {
        eventBroker.notifySduSensorDataCapture(sensorAddress, data);
    }

    @Override
    public void actuatorStateChanged(int actuatorAddress, int newState) {
        eventBroker.notifyActuatorStateChange(actuatorAddress, newState);
    }
}
