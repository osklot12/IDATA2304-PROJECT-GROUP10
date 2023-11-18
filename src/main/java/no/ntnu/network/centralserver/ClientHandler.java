package no.ntnu.network.centralserver;

import no.ntnu.network.ControlProcessAgent;
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
public class ClientHandler extends ControlProcessAgent<ServerContext> implements Runnable, ConnServiceShutdownListener {
    private static final ByteSerializerVisitor SERIALIZER = new NofspSerializer();
    private static final NofspServerDeserializer DESERIALIZER = new NofspServerDeserializer();
    private static final long HEARTBEAT_INTERVAL = 20000;
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
        establishHeartBeater();
        this.context = new ServerContext(this, centralHub, clientSocket.getRemoteSocketAddress().toString());
    }

    /**
     * Establishes the heart beating mechanism.
     */
    private void establishHeartBeater() {
        HeartBeater heartBeater = new HeartBeater(this, HEARTBEAT_INTERVAL);
        heartBeater.addShutdownListener(this);
        addConnectionService(new HeartBeater(this, HEARTBEAT_INTERVAL));
    }

    @Override
    public void run() {
        if (establishConnection(SERIALIZER, DESERIALIZER)) {
            Logger.info("Successfully connected to client " + getRemoteSocketAddress());
        }
    }

    @Override
    protected void handleEndOfMessageStream() {
        Logger.error("Connection to " + getRemoteSocketAddress() + " has been lost.");
    }

    @Override
    protected void handleMessageReadingException(IOException e) {
        Logger.error("Connection to " + getRemoteSocketAddress() + " has been lost: " + e.getMessage());
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
    public void connectionServiceShutdown(ConnectionService service) {
        if (isConnected()) {
            ServerLogger.emergency("Heartbeat service has shutdown, and the connection is therefore closing...");
            safelyClose();
        }
    }
}
