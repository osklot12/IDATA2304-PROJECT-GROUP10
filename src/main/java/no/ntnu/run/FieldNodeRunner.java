package no.ntnu.run;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.client.FieldNodeClient;

public class FieldNodeRunner {
    public static void main(String[] args) {
        // Example usage of FieldNodeClient
        FieldNode fieldNode = new FieldNode(new Environment());
        FieldNodeClient fieldNodeClient = new FieldNodeClient(fieldNode);
        fieldNodeClient.connect("localhost");
    }

}
