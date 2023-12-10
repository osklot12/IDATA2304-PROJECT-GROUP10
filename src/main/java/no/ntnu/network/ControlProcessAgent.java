package no.ntnu.network;

import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.decrypt.PlainTextDecryption;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.PlainTextEncryption;
import no.ntnu.tools.logger.SimpleLogger;
import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.connectionservice.requestmanager.RequestManager;
import no.ntnu.network.connectionservice.requestmanager.RequestTimeoutListener;
import no.ntnu.network.controlprocess.*;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An agent responsible for handling control message communication with another entity in the network.
 * The class provides mechanisms for connecting to a remote socket, maintaining the connection and closing it with
 * a graceful shutdown.
 * <p/>
 * The ControlProcessAgent is abstract and designed as a base class for all network communication entities in the
 * application. It leaves several methods for implementation by the concrete entities, allowing for custom handling
 * of several events. {@code ConnectionService} instances, which can be integrated by the concrete classes,
 * are run when connecting to the socket and closed during a graceful shutdown.
 *
 * @param <C> the type of message processing context to use
 */
public abstract class ControlProcessAgent<C extends MessageContext> implements ControlCommAgent, RequestTimeoutListener {
    private static final long PENDING_REQUEST_TTL = 3000;
    private final List<ConnectionService> connectionServices;
    private TcpControlProcess<C> controlProcess;
    private volatile boolean connected;
    protected Socket socket;
    private volatile int clientNodeAddress;
    protected RequestManager requestManager;
    private final Set<SimpleLogger> loggers;
    private boolean messagesAreEncrypted;

    /**
     * Creates a new CommunicationAgent.
     */
    protected ControlProcessAgent() {
        this.connectionServices = new ArrayList<>();
        this.connected = false;
        this.clientNodeAddress = -1;
        this.loggers = new HashSet<>();
        this.messagesAreEncrypted = false;
    }

    /**
     * Adds a logger for network events.
     *
     * @param logger the logger to add
     */
    public void addLogger(SimpleLogger logger) {
        loggers.add(logger);
    }

    /**
     * Logs info.
     *
     * @param message the information to log
     */
    protected void logInfo(String message) {
        loggers.forEach(logger -> logger.logInfo(message));
    }

    /**
     * Logs an error.
     *
     * @param error the error message to log
     */
    protected void logError(String error) {
        loggers.forEach(logger -> logger.logError(error));
    }

    /**
     * Establishes connection to the socket.
     * Requires that the socket has been set.
     *
     * @param serializer the serializer to use for serializing messages
     * @param deserializer the deserializer to use for deserializing messages
     * @return true if connection was successfully established
     */
    protected boolean establishConnection(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        if (socket == null) {throw new IllegalStateException("Please set socket before trying to establish connection.");}

        if (socket.isClosed()) {throw new IllegalStateException("Cannot establish connection to socket, because socket is closed.");}

        if (isConnected()) {throw new IllegalStateException("Cannot establish connection while already connected.");}

        boolean success = false;

        logInfo("Connecting to " + socket.getRemoteSocketAddress() + "...");
        if (establishControlProcess(serializer, deserializer)) {
            connected = true;
            createConnectionServices();
            startConnectionServices();
            startHandlingReceivedMessages();
            success = true;
        }

        return success;
    }

    /**
     * Creates the connection services for the connection.
     */
    private void createConnectionServices() {
        requestManager = new RequestManager();
        requestManager.addListener(this);
        addConnectionService(requestManager);
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

        if (isConnected()) {
            throw new IllegalStateException("Cannot set socket while connected.");
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
            controlProcess = new TcpControlProcess<>(socket, serializer, deserializer);
            success = true;
            logInfo("Control process for " + getRemoteSocketAddress() + " has been established successfully.");
        } catch (IOException e) {
           logError("Cannot establish control process: " + e.getMessage());
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
            safelyClose();
        }
    }

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
    public RequestMessage acceptResponse(ResponseMessage response) {
        return requestManager.pullRequest(response.getId());
    }

    @Override
    public String getRemoteEntityAsString() {
        return getRemoteSocketAddress().toString();
    }

    @Override
    public int getClientNodeAddress() {
        return clientNodeAddress;
    }

    @Override
    public void setClientNodeAddress(int address) {
        clientNodeAddress = address;
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
            handleConnectionClosing();
        } catch (IOException e) {
            logError("Cannot close the connection with " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }

    /**
     * Handles further processing of a connection closing.
     */
    protected abstract void handleConnectionClosing();

    /**
     * Returns whether the agent is connected or not, in a synchronized manner.
     *
     * @return true if connected
     */
    public synchronized boolean isConnected() {
        return connected;
    }

    /**
     * Safely closes the connection.
     */
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

    /**
     * Returns the loggers for the agent.
     *
     * @return the loggers
     */
    public Set<SimpleLogger> getLoggers() {
        return loggers;
    }

    @Override
    public void setEncryption(EncryptionStrategy encryption) {
        controlProcess.setEncryption(encryption);
    }

    @Override
    public boolean receivedMessagesSecure() {
        return messagesAreEncrypted;
    }

    @Override
    public void setDecryption(DecryptionStrategy decryption) {
        controlProcess.setDecryption(decryption);
        if (!messagesAreEncrypted && !(decryption instanceof PlainTextDecryption)) {
            messagesAreEncrypted = true;
        }
    }
}
