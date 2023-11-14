package no.ntnu.run;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.client.FieldNodeClient;

public class FieldNodeRunner {
    public static void main(String[] args) {
        // Example usage of FieldNodeClient
        FieldNode fieldNode = new FieldNode(/* Add required parameters */);
        FieldNodeClient fieldNodeClient = new FieldNodeClient(fieldNode);

    }

}
