package no.ntnu.run;

import no.ntnu.network.client.FieldNodeClient;
import no.ntnu.tools.FieldNodeClientGenerator;
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
    /**
     * The entrypoint for connecting simulated field nodes to the central server.
     * Keep in mind that the central server must be running in order to
     *
     * @param args console line arguments
     */
    public static void main(String[] args) {
        getFieldNodeClients().forEach(client -> {
            client.addLogger(new ReferencedSystemOutLogger(client.getName()));
            client.connect(CentralServerRunner.IP_ADDRESS);
        });
    }

    /**
     * Returns a list of multiple simulated field node clients.
     *
     * @return a list of field node clients
     */
    private static List<FieldNodeClient> getFieldNodeClients() {
        List<FieldNodeClient> runList = new ArrayList<>();

        runList.add(FieldNodeClientGenerator.getTomatoClientOne());
        runList.add(FieldNodeClientGenerator.getTomatoClientTwo());
        runList.add(FieldNodeClientGenerator.getFlowerClientOne());
        runList.add(FieldNodeClientGenerator.getFlowerClientTwo());
        runList.add(FieldNodeClientGenerator.getSecretBasementClient());

        return runList;
    }
}
