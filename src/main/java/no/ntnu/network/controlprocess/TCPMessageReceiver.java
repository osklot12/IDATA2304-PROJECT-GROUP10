package no.ntnu.network.controlprocess;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.serialize.tool.InputStreamByteSource;
import no.ntnu.network.message.serialize.tool.TlvReader;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Receives TCP (control) messages from another node in the network.
 */
public class TCPMessageReceiver<C extends MessageContext> {
    private final Socket socket;
    private final TlvReader socketReader;
    private final Queue<Message<C>> queue;
    private final MessageDeserializer<C> deserializer;

    /**
     * Creates a new TCPMessageReceiver.
     *
     * @param socket the socket to receive messages from
     * @param deserializer the deserializer used to deserialize messages
     */
    public TCPMessageReceiver(Socket socket, MessageDeserializer<C> deserializer) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageReceiver, because socket is null");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageReceiver, because deserializer is null");
        }

        this.socket = socket;
        this.queue = new ConcurrentLinkedQueue<>();
        this.deserializer = deserializer;
        this.socketReader = new TlvReader(new InputStreamByteSource(socket.getInputStream()), deserializer.getTlvFrame());
        run();
    }

    /**
     * Returns the next received message.
     *
     * @return next message, null if no more messages are received
     */
    public Message<C> getNextMessage() {
        return queue.poll();
    }

    /**
     * Runs the message receiver.
     */
    private synchronized void run() {
        Thread messageReceivingThread = new Thread(() -> {
            while (!socket.isClosed()) {
                queue.add(receiveMessage());
            }
        });

        messageReceivingThread.start();
    }

    /**
     * Receives a message from the socket.
     */
    private Message<C> receiveMessage() {
        Message<C> receivedMessage = null;

        try {
            receivedMessage = deserializer.deserializeMessage(socketReader.readNextTlv());
        } catch (IOException e) {
            Logger.error("Cannot receive message: " + e.getMessage());
        }

        return receivedMessage;
    }
}
