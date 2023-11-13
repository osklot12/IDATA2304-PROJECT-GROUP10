package no.ntnu.network.centralserver;

import no.ntnu.network.message.Message;
import no.ntnu.network.controlprocess.TCPMessageReceiver;
import no.ntnu.network.controlprocess.TCPMessageSender;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A class responsible for handling all communication with a client.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ByteSerializerVisitor serializer;
    private final ByteDeserializer deserializer;
    private TCPMessageReceiver controlMessageReceiver;
    private TCPMessageSender controlMessageSender;


    /**
     * Creates a new ClientHandler.
     *
     * @param clientSocket the client to handle
     * @param serializer the serializer used to send messages
     * @param deserializer the deserializer used to deserialize received messages
     */
    public ClientHandler(Socket clientSocket, ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        if (clientSocket == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because client socket is null.");
        }

        if (serializer == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because serializer is null.");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because deserializer is null");
        }

        this.clientSocket = clientSocket;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void run() {
        if (establishControlProcess()) {
            Message clientMessage = controlMessageReceiver.getNextMessage();

        } else {
            Logger.error("Cannot establish streams for client socket.");
        }
    }

    /**
     * Establishes the control message process for the client handler, used to send and receive control messages.
     *
     * @return true if successfully established
     */
    private boolean establishControlProcess() {
        boolean success = false;

        try {
            controlMessageReceiver = new TCPMessageReceiver(clientSocket, deserializer);
            controlMessageSender = new TCPMessageSender(clientSocket, serializer);
            success = true;
            Logger.info("Control message process successfully established for client " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            Logger.error("Cannot establish control message process for client: " + e.getMessage());
        }

        return success;
    }
}
