package no.ntnu.network.client;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.NofspFieldNodeDeserializer;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;

/**
 * A client for a field node, connecting it to a central server using NOFSP.
 * The class is necessary for a field node to be able to push sensor data and share actuator control in the
 * network.
 */
public class FieldNodeClient extends Client<FieldNodeContext> {
    private final ByteSerializerVisitor serializer;
    private final MessageDeserializer<FieldNodeContext> deserializer;
    private final FieldNode fieldNode;
    private final FieldNodeContext fieldNodeContext;

    /**
     * Creates a new FieldNodeClient.
     *
     * @param fieldNode the field node
     */
    public FieldNodeClient(FieldNode fieldNode) {
        super();
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeClient, because field node is null.");
        }

        serializer = new NofspSerializer();
        deserializer = new NofspFieldNodeDeserializer();
        this.fieldNode = fieldNode;
        this.fieldNodeContext = new FieldNodeContext(this, fieldNode);
    }

    @Override
    public void connect(String serverAddress) {
        if (isConnected()) {
            throw new IllegalStateException("Cannot connect field node, because it is already connected.");
        }

        if (connectToServer(serverAddress, CentralServer.PORT_NUMBER, serializer, deserializer)) {
            // connected and needs to register before using services of server
            registerFieldNode();
        }
    }

    /**
     * Registers the field node at the server.
     */
    private void registerFieldNode() {

    }

    @Override
    protected void processReceivedMessage(Message<FieldNodeContext> message) {

    }

    @Override
    public void disconnect() {

    }
}
