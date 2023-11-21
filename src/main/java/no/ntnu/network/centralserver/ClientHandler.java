package no.ntnu.network.centralserver;

import no.ntnu.network.ControlProcessAgent;
import no.ntnu.network.ServerAgent;
import no.ntnu.network.connectionservice.ClientGate;
import no.ntnu.network.connectionservice.ConnServiceShutdownListener;
import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.connectionservice.HeartBeater;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.NofspServerDeserializer;
import no.ntnu.network.message.request.HeartbeatRequest;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;
import no.ntnu.tools.ServerLogger;

import java.io.IOException;
import java.net.Socket;

/**
 * A class responsible for handling all communication for a server with a single client.
 */
public class ClientHandler extends ControlProcessAgent<ServerContext> implements ServerAgent, Runnable, ConnServiceShutdownListener {
    private static final ByteSerializerVisitor SERIALIZER = new NofspSerializer();
    private static final NofspServerDeserializer DESERIALIZER = new NofspServerDeserializer();
    private static final long HEARTBEAT_INTERVAL = 10000;
    private static final long CLIENT_ACCEPTANCE_PHASE = 3000;
    private ClientGate clientGate;
    private final ServerContext context;
    private volatile boolean clientRegistered;

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
        establishConnectionServices();
        this.context = new ServerContext(this, centralHub);
    }

    /**
     * Establishes all connection services for the client handler.
     */
    private void establishConnectionServices() {
        establishHeartBeater();
        establishClientGate();
    }

    /**
     * Establishes the heart beating service.
     */
    private void establishHeartBeater() {
        HeartBeater heartBeater = new HeartBeater(this, HEARTBEAT_INTERVAL);
        heartBeater.addShutdownListener(this);
        addConnectionService(heartBeater);
    }

    /**
     * Establishes the client gate service.
     */
    private void establishClientGate() {
        clientGate = new ClientGate(this, CLIENT_ACCEPTANCE_PHASE);
        addConnectionService(clientGate);
    }

    @Override
    public synchronized boolean isClientRegistered() {
        return clientRegistered;
    }

    @Override
    public void run() {
        if (establishConnection(SERIALIZER, DESERIALIZER)) {
            Logger.info("Successfully connected to client " + getRemoteSocketAddress());
        }
    }

    @Override
    protected void handleEndOfMessageStream() {
        Logger.error("End of the message stream for " + getRemoteEntityAsString() + " has been met, and the" +
                " connection will therefore be closed.");
    }

    @Override
    protected void handleMessageReadingException(IOException e) {
        Logger.error("An exception has been encountered while reading messages from " + getRemoteEntityAsString() +
                " and the connection will therefore be closed: " + e.getMessage());
    }

    @Override
    protected void processReceivedMessage(Message<ServerContext> message) {
        try {
            message.process(context);
        } catch (IOException e) {
            Logger.error("Cannot process message: " + e.getMessage());
        }
    }

    @Override
    protected void logDisconnection() {
        Logger.info("Client " + getRemoteEntityAsString() + " has been disconnected.");
    }

    @Override
    protected void logSendRequestMessage(RequestMessage request) {
        ServerLogger.requestSent(request, getRemoteSocketAddress().toString());
    }

    @Override
    protected void logSendResponseMessage(ResponseMessage response) {
        ServerLogger.responseSent(response, getRemoteSocketAddress().toString());
    }

    @Override
    public void requestTimedOut(RequestMessage requestMessage) {
        ServerLogger.requestTimeout(requestMessage, getRemoteSocketAddress().toString());

        // checks if the timed out request is a heartbeat
        if (NofspSerializationConstants.HEART_BEAT.equals(requestMessage.getCommand().getString())) {
            // timed out heartbeats closes the connection
            safelyClose();
            ServerLogger.deadHeartbeat(getRemoteSocketAddress().toString());
        }
    }

    @Override
    public void connectionServiceShutdown(String message) {
        if (isConnected()) {
            ServerLogger.emergency(message);
            safelyClose();
        }
    }

    @Override
    public void registerClient() {
        if (!(isConnected())) {
            throw new IllegalStateException("Cannot register client, because agent is not connected yet.");
        }

        clientGate.stop();
        clientRegistered = true;
    }
}
