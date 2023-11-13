package no.ntnu.network.client;

import no.ntnu.network.message.TCPMessageReceiver;
import no.ntnu.network.message.TCPMessageSender;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A client for communicating with a server.
 */
public abstract class Client {
    protected Socket socket;
    protected TCPMessageReceiver controlMessageReceiver;
    protected TCPMessageSender controlMessageSender;

    /**
     * Establishes a connection to a server.
     *
     * @param serverAddress the ip address of the server
     * @param portNumber the port number of the server
     * @param serializer the serializer to use for sending messages
     * @param deserializer the deserializer to use for receiving messages
     */
    protected boolean establishConnectionToServer(String serverAddress, int portNumber,
                                                  ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        if (serializer == null) {
            throw new IllegalArgumentException("Cannot establish connection, because serializer is null.");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot establish connection, because deserializer is null");
        }

        return establishSocket(serverAddress, portNumber) && establishControlProcess(serializer, deserializer);
    }

    /**
     * Establishes a socket.
     *
     * @param serverAddress the ip server address
     * @param portNumber the port number
     * @return true if socket is successfully established
     */
    private boolean establishSocket(String serverAddress, int portNumber) {
        boolean success = false;

        try {
            socket = new Socket(serverAddress, portNumber);
            success = true;
            Logger.info("Connected to the server at " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            Logger.error("Cannot establish socket: " + e.getMessage());
        }

        return success;
    }

    /**
     * Establishes the control message process for the client, used to send and receive control messages.
     *
     * @return true if control message process is successfully established
     */
    private boolean establishControlProcess(ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        boolean success = false;

        try {
            controlMessageReceiver = new TCPMessageReceiver(socket, deserializer);
            controlMessageSender = new TCPMessageSender(socket, serializer);
            success = true;
            Logger.info("Control message process successfully established");
        } catch (Exception e) {
            Logger.error("Cannot establish control message process: " + e.getMessage());
        }

        return success;
    }

    /**
     * Connects to the server.
     *
     * @param serverAddress the ip address of the server
     */
    public abstract void connect(String serverAddress);

    /**
     * Disconnects from the server
     */
    public abstract void disconnect();
}
