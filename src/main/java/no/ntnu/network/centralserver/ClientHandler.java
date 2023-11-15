package no.ntnu.network.centralserver;

import no.ntnu.network.ControlProcessAgent;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.NofspServerDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A class responsible for handling all communication with a single client.
 */
public class ClientHandler extends ControlProcessAgent<ServerContext> implements Runnable {
    private static final ByteSerializerVisitor SERIALIZER = NofspSerializer.getInstance();
    private static final NofspServerDeserializer DESERIALIZER = new NofspServerDeserializer();
    private final ServerContext context;

    /**
     * Creates a new ClientHandler.
     *
     * @param clientSocket the client to handle
     * @param centralHub the central hub
     */
    public ClientHandler(Socket clientSocket, CentralHub centralHub) {
        if (clientSocket == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because client socket is null.");
        }

        setSocket(clientSocket);
        this.context = new ServerContext(this, centralHub);
    }

    @Override
    public void run() {
        if (establishConnection(SERIALIZER, DESERIALIZER)) {
            Logger.info("Successfully connected to client " + getRemoteSocketAddress());
        }
    }

    @Override
    protected void processReceivedMessage(Message<ServerContext> message) {
        Logger.info("Received message '" + message.toString() + "' from client "+ getRemoteSocketAddress());
        try {
            message.process(context);
        } catch (IOException e) {
            Logger.error("Cannot process message: " + e.getMessage());
        }
    }
}
