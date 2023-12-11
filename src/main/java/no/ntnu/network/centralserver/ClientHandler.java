package no.ntnu.network.centralserver;

import no.ntnu.network.ControlProcessAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.sensordataprocess.UdpDataCommAgentProvider;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.connectionservice.ClientGate;
import no.ntnu.network.connectionservice.ConnServiceShutdownListener;
import no.ntnu.network.connectionservice.HeartBeater;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.sensordataprocess.UdpSensorDataPusher;
import no.ntnu.tools.eventformatter.ServerEventFormatter;

import java.io.IOException;
import java.net.Socket;

/**
 * Responsible for handling all communication with a single client, acting as a communication agent for that client.
 */
public class ClientHandler extends ControlProcessAgent<ServerContext> implements Runnable, ConnServiceShutdownListener, UdpDataCommAgentProvider {
    private static final long HEARTBEAT_INTERVAL = 15000;
    private static final long CLIENT_ACCEPTANCE_PHASE = 3000;
    private final ByteSerializerVisitor serializer;
    private final MessageDeserializer<ServerContext> deserializer;
    private ClientGate clientGate;
    private final ServerContext context;

    /**
     * Creates a new ClientHandler.
     *
     * @param clientSocket the client to handle
     * @param centralHub the central hub
     */
    public ClientHandler(Socket clientSocket, CentralHub centralHub, ByteSerializerVisitor serializer, MessageDeserializer<ServerContext> deserializer) {
        super();
        if (clientSocket == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because client socket is null.");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because centralHub is null.");
        }

        if (serializer == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because serializer is null.");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because deserializer is null.");
        }

        this.serializer = serializer;
        this.deserializer = deserializer;
        setSocket(clientSocket);
        establishConnectionServices();
        this.context = new ServerContext(this, this, centralHub, getLoggers());
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
    public void run() {
        if (establishConnection(serializer, deserializer)) {
            logInfo("Successfully connected to client " + getRemoteSocketAddress());
        }
    }

    @Override
    protected void handleMessageReadingException(IOException e) {
        logError("An exception has been encountered while reading messages from " + getRemoteEntityAsString() +
                " and the connection will therefore be closed: " + e.getMessage());
    }

    @Override
    protected void processReceivedMessage(Message<ServerContext> message) {
        try {
            message.process(context);
        } catch (IOException e) {
            logError("Cannot process message: " + e.getMessage());
        }
    }

    @Override
    protected void handleConnectionClosing() {
        logInfo("Client " + getRemoteEntityAsString() + " has been disconnected.");
        // if client is registered
        if (getClientNodeAddress() != -1) {
            context.deregisterClient();
        }
    }

    @Override
    protected void logSendRequestMessage(RequestMessage request) {
        logInfo(ServerEventFormatter.requestSent(request, getRemoteSocketAddress().toString()));
    }

    @Override
    protected void logSendResponseMessage(ResponseMessage response) {
        logInfo(ServerEventFormatter.responseSent(response, getRemoteSocketAddress().toString()));
    }

    @Override
    public void requestTimedOut(RequestMessage requestMessage) {
        logError(ServerEventFormatter.requestTimeout(requestMessage, getRemoteSocketAddress().toString()));

        // checks if the timed out request is a heartbeat
        if (NofspSerializationConstants.HEART_BEAT.equals(requestMessage.getCommand().getString())) {
            // timed out heartbeats closes the connection
            safelyClose();
            ServerEventFormatter.deadClient(getRemoteSocketAddress().toString());
        }
    }

    @Override
    public void connectionServiceShutdown(String message) {
        if (isConnected()) {
            ServerEventFormatter.emergency(message);
            safelyClose();
        }
    }

    @Override
    public void setClientNodeAddress(int address) {
        super.setClientNodeAddress(address);
        clientGate.stop();
    }

    @Override
    public DataCommAgent getDataCommAgent(int portNumber) throws IOException {
        return new UdpSensorDataPusher(socket.getInetAddress(), portNumber, serializer);
    }
}
