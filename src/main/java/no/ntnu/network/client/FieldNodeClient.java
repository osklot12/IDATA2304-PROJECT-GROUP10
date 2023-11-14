package no.ntnu.network.client;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.deserialize.NofspDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.*;

public class FieldNodeClient extends Client {

    private static final ByteSerializerVisitor SERIALIZER = NofspSerializer.getInstance();
    private static final ByteDeserializer DESERIALIZER = NofspDeserializer.getInstance();

    private final FieldNode fieldNode;


    /**
     * Initializes a new instance of the FieldNodeClient class.
     * @param fieldNode    The associated field node.
     */
    public FieldNodeClient(FieldNode fieldNode) {
        super();
        this.fieldNode = fieldNode;
    }




    @Override
    protected void processReceivedMessage(Message message) {
        // Handle received messages specific to FieldNodeClient
        // Implement this method based on your requirements

    }

    @Override
    public void connect(String serverAddress) {
        if (connected()) {
            throw new IllegalStateException("Cannot connect control panel, because it is already connected.");
        }

        if (connectToServer(serverAddress, CentralServer.PORT_NUMBER, SERIALIZER, DESERIALIZER)) {
            registerFieldNode();
        }

    }

    private void registerFieldNode() {
        //send the FNST and the FNAT
    }


    @Override
    public void disconnect() {
    }



//    /**
//     * Closes the client socket connection.
//     */
//    public void closeConnection() {
//        try {
//            if (clientSocket != null) {
//                clientSocket.close();
//            }
//        } catch (IOException e) {
//            Logger.error("Failed to close the client socket: " + e.getMessage());
//        }
//    }

}

