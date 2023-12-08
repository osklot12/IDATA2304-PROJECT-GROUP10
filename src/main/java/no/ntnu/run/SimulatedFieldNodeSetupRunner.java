package no.ntnu.run;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.FieldNodeBuilder;
import no.ntnu.network.client.FieldNodeClient;
import no.ntnu.tools.logger.ReferencedSystemOutLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * A class simulating a setup of multiple field nodes connected to the central server.
 * Subgroups of the field nodes are set up for the same virtual environment (indicated by their name), to better
 * demonstrate how actuators affect the whole environment, and not only single field nodes.
 * <p>
 * Field nodes can be run individually using the {@code FieldNodeRunner} class, but that does not provide
 * field node diversity nor multiple field nodes for the same environment.
 * </p>
 */
public class SimulatedFieldNodeSetupRunner {
    private static final int SENSOR_NOISE = 1; // level of sensor noise for all sensors
    private static final Environment TOMATO_GREENHOUSE = new Environment();
    private static final Environment FLOWER_GREENHOUSE = new Environment();
    private static final Environment SECRET_BASEMENT_GREENHOUSE = new Environment();

    /**
     * The entrypoint for connecting simulated field nodes to the central server.
     * Keep in mind that the central server must be running in order to
     *
     * @param args console line arguments
     */
    public static void main(String[] args) {
        getFieldNodeClients().forEach(client -> {
            client.connect(CentralServerRunner.IP_ADDRESS);
            client.addLogger(new ReferencedSystemOutLogger(client.getName()));
        });
    }

    private static List<FieldNodeClient> getFieldNodeClients() {
        List<FieldNodeClient> runList = new ArrayList<>();

        runList.add(getTomatoClientOne());
        runList.add(getTomatoClientTwo());
        runList.add(getFlowerClientOne());
        runList.add(getFlowerClientTwo());
        runList.add(getSecretBasementClient());

        return runList;
    }

    private static FieldNodeClient getTomatoClientOne() {
        FieldNode tomatoNodeOne = new FieldNodeBuilder(TOMATO_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addTemperatureSensor(SENSOR_NOISE).addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE)
                .addAirConditioner().addDimmer().build();

        return new FieldNodeClient(tomatoNodeOne, "Tomato house node 1");
    }

    private static FieldNodeClient getTomatoClientTwo() {
        FieldNode tomatoNodeTwo = new FieldNodeBuilder(TOMATO_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addAirConditioner().addHumidifier().addDimmer().addDimmer().build();

        return new FieldNodeClient(tomatoNodeTwo, "Tomato house node 2");
    }

    private static FieldNodeClient getFlowerClientOne() {
        FieldNode flowerNodeOne = new FieldNodeBuilder(FLOWER_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addHumidifier().addDimmer().build();

        return new FieldNodeClient(flowerNodeOne, "Flower house node 1");
    }

    private static FieldNodeClient getFlowerClientTwo() {
        FieldNode flowerNodeTwo = new FieldNodeBuilder(FLOWER_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addHumiditySensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addHumidifier().addDimmer().addDimmer().addHumidifier().build();

        return new FieldNodeClient(flowerNodeTwo, "Flower house node 2");
    }

    private static FieldNodeClient getSecretBasementClient() {
        FieldNode secretNode = new FieldNodeBuilder(SECRET_BASEMENT_GREENHOUSE).addTemperatureSensor(SENSOR_NOISE)
                .addTemperatureSensor(SENSOR_NOISE).addLuminositySensor(SENSOR_NOISE).addAirConditioner()
                .addAirConditioner().build();

        return new FieldNodeClient(secretNode, "Secret basement node");
    }
}
