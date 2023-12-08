package no.ntnu.tools;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.FieldNodeBuilder;
import no.ntnu.network.client.FieldNodeClient;

/**
 * Generates field node clients put in simulated environments.
 */
public class FieldNodeClientGenerator {
    private static final int SENSOR_NOISE = 1; // level of sensor noise for all sensors
    private static final Environment TOMATO_GREENHOUSE = new Environment();
    private static final Environment FLOWER_GREENHOUSE = new Environment();
    private static final Environment SECRET_BASEMENT_GREENHOUSE = new Environment();

    /**
     * Does not allow creating instances of the class.
     */
    private FieldNodeClientGenerator() {}

    /**
     * Generates and returns a field node client for the tomato greenhouse.
     *
     * @return the field node client
     */
    public static FieldNodeClient getTomatoClientOne() {
        FieldNode tomatoNodeOne = new FieldNodeBuilder(TOMATO_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addTemperatureSensor(SENSOR_NOISE).addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE)
                .addAirConditioner().addDimmer().build();

        return new FieldNodeClient(tomatoNodeOne, "Tomato house node 1");
    }

    /**
     * Generates and returns a field node client for the tomato greenhouse.
     *
     * @return the field node client
     */
    public static FieldNodeClient getTomatoClientTwo() {
        FieldNode tomatoNodeTwo = new FieldNodeBuilder(TOMATO_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addAirConditioner().addHumidifier().addDimmer().addDimmer().build();

        return new FieldNodeClient(tomatoNodeTwo, "Tomato house node 2");
    }

    /**
     * Generates and returns a field node client for the flower greenhouse.
     *
     * @return the field node client
     */
    public static FieldNodeClient getFlowerClientOne() {
        FieldNode flowerNodeOne = new FieldNodeBuilder(FLOWER_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addHumidifier().addDimmer().build();

        return new FieldNodeClient(flowerNodeOne, "Flower house node 1");
    }

    /**
     * Generates and returns a field node client for the flower greenhouse.
     *
     * @return the field node client
     */
    public static FieldNodeClient getFlowerClientTwo() {
        FieldNode flowerNodeTwo = new FieldNodeBuilder(FLOWER_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addHumidifier().addDimmer().addDimmer().addHumidifier().build();

        return new FieldNodeClient(flowerNodeTwo, "Flower house node 2");
    }

    /**
     * Generates and returns a field node client for the secret basement.
     *
     * @return tje field node client
     */
    public static FieldNodeClient getSecretBasementClient() {
        FieldNode secretNode = new FieldNodeBuilder(SECRET_BASEMENT_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addTemperatureSensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addAirConditioner().build();

        return new FieldNodeClient(secretNode, "Secret basement node");
    }
}
