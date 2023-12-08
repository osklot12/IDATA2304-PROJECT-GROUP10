package no.ntnu.fieldnode;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.actuator.FanActuator;
import no.ntnu.fieldnode.device.actuator.HumidifierActuator;
import no.ntnu.fieldnode.device.actuator.LightDimmerActuator;
import no.ntnu.fieldnode.device.sensor.HumiditySensor;
import no.ntnu.fieldnode.device.sensor.LuminositySensor;
import no.ntnu.fieldnode.device.sensor.TemperatureSensor;

/**
 * A builder class for field nodes.
 */
public class FieldNodeBuilder {
    private final FieldNode fieldNode;

    /**
     * Creates a new FieldNodeBuilder.
     *
     * @param environment the environment to set the field node in
     */
    public FieldNodeBuilder(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeBuilder, because environment is null.");
        }

        this.fieldNode = new FieldNode(environment);
    }

    /**
     * Adds a temperature sensor to the field node.
     *
     * @param sensorNoise the sensor noise
     * @return the field node builder
     */
    public FieldNodeBuilder addTemperatureSensor(int sensorNoise) {
        fieldNode.addDevice(new TemperatureSensor(sensorNoise));
        return this;
    }

    /**
     * Adds a humidity sensor to the field node.
     *
     * @param sensorNoise the sensor noise
     * @return the field node builder
     */
    public FieldNodeBuilder addHumiditySensor(int sensorNoise) {
        fieldNode.addDevice(new HumiditySensor(sensorNoise));
        return this;
    }

    /**
     * Adds a luminosity sensor to the field node.
     *
     * @param sensorNoise the sensor noise
     * @return the field node builder
     */
    public FieldNodeBuilder addLuminositySensor(int sensorNoise) {
        fieldNode.addDevice(new LuminositySensor(sensorNoise));
        return this;
    }

    /**
     * Adds a fan actuator to the field node.
     *
     * @return the field node builder
     */
    public FieldNodeBuilder addAirConditioner() {
        fieldNode.addDevice(new FanActuator());
        return this;
    }

    /**
     * Adds a humidifier actuator to the field node.
     *
     * @return the field node builder
     */
    public FieldNodeBuilder addHumidifier() {
        fieldNode.addDevice(new HumidifierActuator());
        return this;
    }

    /**
     * Adds a light dimmer actuator to the field node.
     *
     * @return the field node builder
     */
    public FieldNodeBuilder addDimmer() {
        fieldNode.addDevice(new LightDimmerActuator());
        return this;
    }

    /**
     * Returns the built field node.
     *
     * @return the field node
     */
    public FieldNode build() {
        return fieldNode;
    }
}
