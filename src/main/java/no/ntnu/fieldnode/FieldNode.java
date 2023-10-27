package no.ntnu.fieldnode;

import no.ntnu.fieldnode.device.actuator.Actuator;
import no.ntnu.fieldnode.device.actuator.ActuatorListener;
import no.ntnu.fieldnode.device.sensor.Sensor;
import no.ntnu.fieldnode.device.sensor.SensorListener;
import no.ntnu.environment.Environment;
import no.ntnu.exception.ActuatorInteractionFailedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class representing the logic for a field node in the network.
 * A field node is a subsystem in the network consisting of sensors and actuators.
 */
public class FieldNode implements SensorListener, ActuatorListener {
    private final Environment environment;
    private final Map<Integer, Sensor> sensors;
    private final Map<Integer, Actuator> actuators;

    /**
     * Creates a new FieldNode.
     *
     * @param environment the environment in which to place the field node
     */
    public FieldNode(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Cannot create FieldNode, because environment is null.");
        }

        this.environment = environment;
        this.sensors = new HashMap<>();
        this.actuators = new HashMap<>();
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
     * Adds a sensor to the field node, assigning it an address automatically.
     *
     * @param sensor the sensor to add
     * @return the address of the sensor, -1 on error
     */
    public int addSensor(Sensor sensor) {
        int address = -1;

        if (sensor.connectToFieldNode(this)) {
            if (!sensors.containsValue(sensor)) {
                address = generateNewDeviceAddress(sensors.keySet());
                sensors.put(address, sensor);
            } else {
                sensor.disconnectFromFieldNode(this);
            }
        }

        return address;
    }

    /**
     * Adds an actuator to the field node, assigning it an address automatically.
     *
     * @param actuator the actuator to add
     * @return the address of the actuator, -1 on error
     */
    public int addActuator(Actuator actuator) {
        int address = -1;

        if (actuator.connectToFieldNode(this)) {
            if (!actuators.containsValue(actuator)) {
                address = generateNewDeviceAddress(actuators.keySet());
                actuators.put(address, actuator);
            } else {
                actuator.disconnectFromFieldNode(this);
            }
        }

        return address;
    }

    private int generateNewDeviceAddress(Set<Integer> intSet) {
        int currentCheck = 0;

        while (intSet.contains(currentCheck)) {
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
        if (!actuators.containsKey(actuatorAddress)) {
            throw new ActuatorInteractionFailedException("Cannot change state of actuator, because an actuator " +
                    "with the given address does not exist.");
        }

        try {
            actuators.get(actuatorAddress).setState(state);
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
        if (!actuators.containsKey(actuatorAddress)) {
            throw new ActuatorInteractionFailedException("Cannot get the state of actuator, because an actuator with" +
                    " the given address does not exist.");
        }

        return actuators.get(actuatorAddress).getState();
    }

    @Override
    public void sensorDataCaptured(Sensor sensor) {
        System.out.println("Data captured: " + sensor.getSensorData().toFormattedData());
    }

    @Override
    public void actuatorStateChanged(Actuator actuator) {
        System.out.println("State changed to " + actuator.getState());
    }
}
