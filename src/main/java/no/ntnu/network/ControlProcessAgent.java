package no.ntnu.network;

import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.connectionservice.requestmanager.RequestManager;
import no.ntnu.network.connectionservice.requestmanager.RequestTimeoutListener;
import no.ntnu.network.controlprocess.*;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * An agent responsible for handling control message communication with another node in the network.
 *
 * @param <C> the type of message context for the messages
 */
public abstract class ControlProcessAgent<C extends MessageContext> implements CommunicationAgent, RequestTimeoutListener {
    private static final long PENDING_REQUEST_TTL = 3000;
    private final List<ConnectionService> connectionServices;
    private Socket socket;
    private TCPControlProcess<C> controlProcess;
    private RequestManager requestManager;
    private volatile boolean connected;

    /**
     * Creates a new CommunicationAgent.
     */
    protected ControlProcessAgent() {
        this.connectionServices = new ArrayList<>();
        this.connected = false;
    }

    /**
     * Establishes connection to a socket.
     *
     * @param serializer the serializer to use for serializing messages
     * @param deserializer the deserializer to use for deserializing messages
     * @return true if connection was successfully established
     */
    protected boolean establishConnection(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        if (socket == null) {
            throw new IllegalStateException("Please set socket before trying to establish connection.");
        }

        if (socket.isClosed()) {
            throw new IllegalStateException("Cannot establish connection to socket, because socket is closed.");
        }

        boolean success = false;

        Logger.info("Connecting to " + socket.getRemoteSocketAddress() + "...");
        if (establishControlProcess(serializer, deserializer)) {
            connected = true;

            requestManager = new RequestManager();
            requestManager.addListener(this);
            addConnectionService(requestManager);

            startHandlingReceivedMessages();
            startConnectionServices();
            success = true;
        }

        return success;
    }

    /**
     * Sets the socket.
     *
     * @param socket the socket
     */
    protected void setSocket(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot set socket, because it is null");
        }

        if (connected) {
            throw new IllegalStateException("Cannot set socket while already connected.");
        }

        this.socket = socket;
    }

    /**
     * Establishes the TCP control message process, used to send and receive control messages.
     *
     * @return true if control message process is successfully established
     */
    private boolean establishControlProcess(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        boolean success = false;

        try {
            controlProcess = new TCPControlProcess<>(socket, serializer, deserializer);
            success = true;
            Logger.info("Control process for " + getRemoteSocketAddress() + " has been established successfully.");
        } catch (IOException e) {
            Logger.error("Cannot establish control process: " + e.getMessage());
        }

        return success;
    }

    /**
     * Starts all connection services.
     */
    private void startConnectionServices() {
        connectionServices.forEach(ConnectionService::start);
    }

    /**
     * Stops all connection services.
     */
    private void stopConnectionServices() {
        connectionServices.forEach(ConnectionService::stop);
    }

    /**
     * Adds a connection service.
     *
     * @param service connection service to add
     */
    protected void addConnectionService(ConnectionService service) {
        connectionServices.add(service);
    }

    /**
     * Starts handling messages received from the remote socket.
     */
    private synchronized void startHandlingReceivedMessages() {
        Thread receivedMessageHandlingThread = new Thread(() -> {
            try {
                while (isConnected() && !socket.isClosed()) {
                    handleNextMessage();
                }
            } catch (IOException e) {
                if (isConnected()) {
                    handleMessageReadingException(e);
                }
            } finally {
                safelyClose();
            }
        });
        receivedMessageHandlingThread.start();
    }

    /**
     * Handles the next received message.
     */
    private void handleNextMessage() throws IOException {
        Message<C> nextMessage = controlProcess.getNextMessage();
        if (nextMessage != null) {
            processReceivedMessage(nextMessage);
        } else {
            handleEndOfMessageStream();
            close();
        }
    }

    /**
     * Handles the event of meeting the end of the message stream.
     */
    protected abstract void handleEndOfMessageStream();

    /**
     * Handles the case of an I/O exception thrown while trying to read the next message.
     *
     * @param e the I/O exception thrown
     */
    protected abstract void handleMessageReadingException(IOException e);

    /**
     * Processes a received message.
     *
     * @param message received message
     */
    protected abstract void processReceivedMessage(Message<C> message);

    /**
     * Returns the remote socket address.
     *
     * @return the remote socket address
     */
    protected SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void sendRequest(RequestMessage request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("Cannot send request message, because it is null.");
        }

        if (!connected) {
            throw new IOException("No connection established.");
        }

        requestManager.putRequest(request, PENDING_REQUEST_TTL);
        controlProcess.sendMessage(request);
        logSendRequestMessage(request);
    }

    @Override
    public void sendResponse(ResponseMessage response) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Cannot send response message, because it is null");
        }

        if (!connected) {
            throw new IOException("No connection established.");
        }

        controlProcess.sendMessage(response);
        logSendResponseMessage(response);
    }

    @Override
    public boolean acceptResponse(ResponseMessage response) {
        boolean accepted = false;

        RequestMessage correspondingRequest = requestManager.pullRequest(response.getId().getInteger());
        if (correspondingRequest != null) {
            accepted = true;
        }

        return accepted;
    }

    @Override
    public void close() {
        if (!connected) {
            throw new IllegalStateException("Cannot close the connection, because connection is not open.");
        }

        try {
            stopConnectionServices();
            connected = false;
            socket.close();
        } catch (IOException e) {
            Logger.error("Cannot close the connection with " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }

    protected synchronized boolean isConnected() {
        return connected;
    }

    protected synchronized void safelyClose() {
        if (connected) {
            close();
        }
    }

    /**
     * Log the sending of a request message.
     *
     * @param request the request message sent
     */
    protected abstract void logSendRequestMessage(RequestMessage request);

    /**
     * Log the sending of a response message.
     *
     * @param response the response message sent
     */
    protected abstract void logSendResponseMessage(ResponseMessage response);
}
