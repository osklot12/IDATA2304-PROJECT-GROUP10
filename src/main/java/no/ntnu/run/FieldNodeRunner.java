package no.ntnu.run;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.FieldNodeFactory;
import no.ntnu.network.client.FieldNodeClient;

public class FieldNodeRunner {
    public static void main(String[] args) {
        Environment environment = new Environment();
        FieldNode fieldNode = FieldNodeFactory.getSomeNoiseFieldNode(environment);
        FieldNodeClient client = new FieldNodeClient(fieldNode);
        client.connect("localhost");

        try {
            Thread.sleep(10000);
            fieldNode.setActuatorState(3, 3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
