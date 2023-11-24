package no.ntnu.fieldnode;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.actuator.FanActuator;
import no.ntnu.fieldnode.device.actuator.HumidifierActuator;
import no.ntnu.fieldnode.device.actuator.LightDimmerActuator;
import no.ntnu.fieldnode.device.sensor.HumiditySensor;
import no.ntnu.fieldnode.device.sensor.LuminositySensor;
import no.ntnu.fieldnode.device.sensor.TemperatureSensor;

/**
 * A factory for creating field nodes.
 */
public class FieldNodeFactory {
    private static final String NO_ENVIRONMENT_EXCEPTION_DESCRIPTION
            = "Cannot create field node, because environment is null.";

    /**
     * Does not allow creating instances of the class.
     */
    private FieldNodeFactory() {}

    /**
     * Creates a FieldNode with no sensor noise.
     *
     * @param environment the environment for the field node
     * @return field node with no sensor noise
     */
    public static FieldNode getNoNoiseFieldNode(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException(NO_ENVIRONMENT_EXCEPTION_DESCRIPTION);
        }

        FieldNode fieldNode = new FieldNode(environment);
        addSensors(fieldNode, 0);
        addActuators(fieldNode);

        return fieldNode;
    }

    /**
     * Creates a FieldNode with some sensor noise.
     *
     * @param environment the environment for the field node
     * @return field node with some sensor noise
     */
    public static FieldNode getSomeNoiseFieldNode(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException(NO_ENVIRONMENT_EXCEPTION_DESCRIPTION);
        }

        FieldNode fieldNode = new FieldNode(environment);
        addSensors(fieldNode, 1);
        addActuators(fieldNode);

        return fieldNode;
    }

    /**
     * Creates a FieldNode with a lot of sensor noise.
     *
     * @param environment the environment for the field node
     * @return field node with a lot of sensor noise
     */
    public static FieldNode getMuchNoiseFieldNode(Environment environment) {
        if (environment == null) {
            throw new IllegalArgumentException(NO_ENVIRONMENT_EXCEPTION_DESCRIPTION);
        }

        FieldNode fieldNode = new FieldNode(environment);
        addSensors(fieldNode, 5);
        addActuators(fieldNode);

        return fieldNode;
    }

    private static void addSensors(FieldNode fieldNode, int noiseLevel) {
        fieldNode.addDevice(new TemperatureSensor(noiseLevel));
        fieldNode.addDevice(new HumiditySensor(noiseLevel));
        fieldNode.addDevice(new LuminositySensor(noiseLevel));
    }

    private static void addActuators(FieldNode fieldNode) {
        fieldNode.addDevice(new FanActuator());
        fieldNode.addDevice(new HumidifierActuator());
        fieldNode.addDevice(new LightDimmerActuator());
    }
}
