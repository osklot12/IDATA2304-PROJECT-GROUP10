package no.ntnu.network.controlprocess;

import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Sends TCP (control) messages to another node in the network.
 */
public class TCPMessageSender {
    private final Socket socket;
    private final ConcurrentLinkedQueue<ControlMessage> queue;
    private final ByteSerializerVisitor serializer;

    /**
     * Creates a new TCPMessageSender.
     *
     * @param socket socket to send messages to
     * @param serializer the serializer to use for serialization of messages
     * @throws IOException thrown if an I/O exception occurs
     */
    public TCPMessageSender(Socket socket, ByteSerializerVisitor serializer) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageSender, because socket is null.");
        }

        if (serializer == null) {
            throw new IllegalArgumentException("Cannot create TCPMessageSender, because serializer is null.");
        }

        this.socket = socket;
        this.queue = new ConcurrentLinkedQueue<>();
        this.serializer = serializer;
        run();
    }

    /**
     * Enqueues a {@code Message}.
     * Once it is the first in the queue, it will be sent to the destination socket.
     *
     * @param message message to enqueue
     * @return true if message is added to the queue
     */
    public boolean enqueueMessage(ControlMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot enqueue message, because message is null.");
        }

        return queue.add(message);
    }

    /**
     * Runs the message sender.
     */
    private synchronized void run () {
        Thread messageSendingThread = new Thread(() -> {
            while (!socket.isClosed()) {
                ControlMessage messageToSend = queue.poll();
                if (messageToSend != null) {
                    sendMessage(messageToSend);
                }
            }
        });

        messageSendingThread.start();
    }

    /**
     * Sends a message to the destination socket.
     *
     * @param messageToSend message to send
     */
    private void sendMessage(ControlMessage messageToSend) {
        try {
            byte[] serializedMessage = serializer.serialize(messageToSend);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(serializedMessage);
            outputStream.flush();
        } catch (IOException e) {
            Logger.error("Cannot send message: " + e.getMessage());
        }
    }
}
