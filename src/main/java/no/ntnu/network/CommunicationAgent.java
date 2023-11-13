package no.ntnu.network;

import no.ntnu.network.controlprocess.PendingRequest;
import no.ntnu.network.controlprocess.PendingRequestMap;
import no.ntnu.network.controlprocess.TCPMessageReceiver;
import no.ntnu.network.controlprocess.TCPMessageSender;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.net.Socket;

/**
 * An agent responsible for handling control message communication with another node in the network.
 */
public abstract class CommunicationAgent {
    private static final long PENDING_REQUEST_TTL = 30000;
    private static final int MESSAGE_ID_POOL = 1000000;
    private Socket socket;
    private TCPMessageReceiver controlMessageReceiver;
    private PendingRequestMap pendingReceivedRequests;
    private TCPMessageSender controlMessageSender;
    private PendingRequestMap pendingSentRequests;
    private int controlMessageIdCounter;
    private volatile boolean connected;

    /**
     * Creates a new CommunicationAgent.
     */
    protected CommunicationAgent() {
        this.pendingSentRequests = new PendingRequestMap(PENDING_REQUEST_TTL);
        this.pendingReceivedRequests = new PendingRequestMap(PENDING_REQUEST_TTL);
        this.controlMessageIdCounter = 0;
        this.connected = false;
    }

    /**
     * Establishes connection to a socket.
     *
     * @param socket socket to connect to
     * @return true if connection was successfully established
     */
    protected boolean establishConnection(Socket socket, ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        if (socket == null) {
            throw new IllegalStateException("Please set socket before trying to establish connection.");
        }

        if (socket.isClosed()) {
            throw new IllegalStateException("Cannot establish connection to socket, because socket is closed.");
        }

        this.socket = socket;
        Logger.info("Connection to " + socket.getRemoteSocketAddress() + "...");
        return establishControlProcess(serializer, deserializer);
    }

    /**
     * Establishes the control message process for the client, used to send and receive control messages.
     *
     * @return true if control message process is successfully established
     */
    private boolean establishControlProcess(ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
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
    private boolean establishTCPSenderAndReceiver(ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        boolean success = false;

        try {
            controlMessageReceiver = new TCPMessageReceiver(socket, deserializer);
            controlMessageSender = new TCPMessageSender(socket, serializer);
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
                Message receivedMessage = controlMessageReceiver.getNextMessage();
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
    protected abstract void processReceivedMessage(Message message);

    /**
     * Sends a message.
     *
     * @param message message to send, cannot be null
     */
    protected void sendControlMessage(ControlMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot send message, because message is null");
        }

        if (controlMessageSender != null) {
            if (message instanceof RequestMessage request) {
                handleRequestMessageDeparture(request);
            }

            controlMessageSender.enqueueMessage(message);
            Logger.info("Message '" + message.toString() + "' with id " + message.getId().toString() + " has been sent.");
        } else {
            Logger.error("Cannot send message because no connection is established.");
        }
    }

    /**
     * Handles the sending of a request message.
     *
     * @param request the request message to handle
     */
    private void handleRequestMessageDeparture(RequestMessage request) {
        if (pendingSentRequests.size() >= MESSAGE_ID_POOL) {
            throw new IllegalStateException("Cannot send request message, because the message id pool is full.");
        }

        assignMessageId(request);
        pendingSentRequests.put(request.getId().getInteger(),
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
        while (pendingSentRequests.containsKey(controlMessageIdCounter)) {
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
}
