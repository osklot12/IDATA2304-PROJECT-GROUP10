package no.ntnu.network.client;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.deserialize.NofspFieldNodeDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;

public class FieldNodeClient extends Client<FieldNodeContext> {

    private static final ByteSerializerVisitor SERIALIZER = new NofspSerializer();
    private static final MessageDeserializer<FieldNodeContext> DESERIALIZER = new NofspFieldNodeDeserializer();

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
    public void connect(String serverAddress) {
        if (isConnected()) {
            throw new IllegalStateException("Cannot connect control panel, because it is already connected.");
        }

        if (connectToServer(serverAddress, CentralServer.PORT_NUMBER, SERIALIZER, DESERIALIZER)) {
            registerFieldNode();
        }

    }

    private void registerFieldNode() {
        //send the FNST and the FNAT
    }

//    private void sendFNST() {
//        // Send the FNST to the server
//        sendControlMessage(new UpdateFNSTRequest(fieldNode.getFNST()));
//    }


    @Override
    public void disconnect() {
    }

    @Override
    protected void processReceivedMessage(Message<FieldNodeContext> message) {

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

