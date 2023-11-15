package no.ntnu.network;

import no.ntnu.network.controlprocess.PendingRequest;
import no.ntnu.network.controlprocess.PendingRequestMap;
import no.ntnu.network.controlprocess.TCPMessageReceiver;
import no.ntnu.network.controlprocess.TCPMessageSender;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * An agent responsible for handling control message communication with another node in the network.
 *
 * @param <C> the type of message context for the messages
 */
public abstract class ControlProcessAgent<C extends MessageContext> implements CommunicationAgent {
    private static final long PENDING_REQUEST_TTL = 3000;
    private static final int MESSAGE_ID_POOL = 1000000;
    private final PendingRequestMap pendingRequests;
    private Socket socket;
    private TCPMessageReceiver<C> messageReceiver;
    private TCPMessageSender messageSender;
    private int controlMessageIdCounter;
    private volatile boolean connected;

    /**
     * Creates a new CommunicationAgent.
     */
    protected ControlProcessAgent() {
        this.pendingRequests = new PendingRequestMap(PENDING_REQUEST_TTL);
        this.controlMessageIdCounter = 0;
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

        Logger.info("Connecting to " + socket.getRemoteSocketAddress() + "...");
        return establishControlProcess(serializer, deserializer);
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

        this.socket = socket;
    }

    /**
     * Establishes the control message process for the client, used to send and receive control messages.
     *
     * @return true if control message process is successfully established
     */
    private boolean establishControlProcess(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        boolean success = false;

        if (establishTCPSenderAndReceiver(serializer, deserializer)) {
            connected = true;
            startHandlingReceivedMessages();
            success = true;
            Logger.info("Successfully connected to " + socket.getRemoteSocketAddress() + ".");
        }

        return success;
    }

    /**
     * Establishes the control message sender and receiver.
     *
     * @param serializer serializer used for message serialization
     * @param deserializer deserializer used for deserialization
     * @return true if successfully established
     */
    private boolean establishTCPSenderAndReceiver(ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        boolean success = false;

        try {
            messageReceiver = new TCPMessageReceiver<>(socket, deserializer);
            messageSender = new TCPMessageSender(socket, serializer);
            success = true;
        } catch (Exception e) {
            Logger.error("Cannot establish control message process: " + e.getMessage());
        }

        return success;
    }

    /**
     * Starts handling messages received from the remote socket.
     */
    private synchronized void startHandlingReceivedMessages() {
        Thread receivedMessageHandlingThread = new Thread(() -> {
            while (connected) {
                Message<C> receivedMessage = messageReceiver.getNextMessage();
                if (receivedMessage != null) {
                    processReceivedMessage(receivedMessage);
                }

                if (socket.isClosed()) {
                    connected = false;
                }
            }
        });
        receivedMessageHandlingThread.start();
    }

    /**
     * Processes a received message.
     *
     * @param message received message
     */
    protected abstract void processReceivedMessage(Message<C> message);

    /**
     * Handles the sending of a request message.
     *
     * @param request the request message to handle
     */
    private void handleRequestMessageDeparture(RequestMessage request) {
        if (pendingRequests.size() >= MESSAGE_ID_POOL) {
            throw new IllegalStateException("Cannot send request message, because the message id pool is full.");
        }

        assignMessageId(request);
        pendingRequests.put(request.getId().getInteger(),
                new PendingRequest(request, System.currentTimeMillis()));
    }

    /**
     * Assigns an ID to a message.
     *
     * @param request request to assign id to
     */
    private void assignMessageId(RequestMessage request) {
        // increments id if it is already assigned to a pending request
        int iterations = 0;
        while (pendingRequests.containsKey(controlMessageIdCounter)) {
            if (iterations > MESSAGE_ID_POOL) {
                throw new IllegalArgumentException("Cannot assign ID to request, because there are no more available IDs.");
            }

            incrementIdCounter();
            iterations++;
        }

        request.setId(controlMessageIdCounter);
    }

    /**
     * Increments the message id counter.
     */
    private void incrementIdCounter() {
        controlMessageIdCounter = ((controlMessageIdCounter + 1) % MESSAGE_ID_POOL);
    }

    /**
     * Returns whether the agent is connected or not.
     *
     * @return true if connected, false otherwise
     */
    public boolean connected() {
        return connected;
    }

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

        handleRequestMessageDeparture(request);
        messageSender.enqueueMessage(request);
    }

    @Override
    public void sendResponse(ResponseMessage response) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Cannot send response message, because it is null");
        }

        if (!connected) {
            throw new IOException("No connection established.");
        }

        messageSender.enqueueMessage(response);
        Logger.info("Response message (id: " + response.getId() + ") has been sent: " + response.toString());
    }

    @Override
    public boolean acceptResponse(ResponseMessage response) {
        boolean accepted = false;

        int responseId = response.getId().getInteger();
        if (pendingRequests.containsKey(responseId)) {
            pendingRequests.remove(responseId);
            accepted = true;
            Logger.info("Request with ID " + responseId + " has received a response: " + response.toString());
        }

        return accepted;
    }

    @Override
    public void close() {
        if (!connected) {
            throw new IllegalStateException("Cannot close the connection, because connection is not open.");
        }

        try {
            socket.close();
            Logger.info("Connection has been closed with " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            Logger.error("Cannot close the connection with " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }
}
