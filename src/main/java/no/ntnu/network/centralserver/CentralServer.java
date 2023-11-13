package no.ntnu.network.centralserver;

import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.deserialize.NofspDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The CentralServer serves as a hub for managing and routing communication between various field nodes and
 * control panels in the network.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>
 *         Receiving incoming data from field nodes and control panels
 *     </li>
 *     <li>
 *         Routing messages to the appropriate destinations
 *     </li>
 *     <li>
 *         Managing the state of all connected devices
 *     </li>
 *     <li>
 *         Handling error conditions and failures
 *     </li>
 * </ul>
 */
public class CentralServer {
    public static final int PORT_NUMBER = 60005;
    private final CentralCore centralCore;
    private final ByteSerializerVisitor serializer;
    private final ByteDeserializer deserializer;
    private volatile boolean running;
    private ServerSocket serverSocket;

    /**
     * Creates a new CentralServer.
     */
    public CentralServer() {
        this.centralCore = new CentralCore();
        serializer = NofspSerializer.getInstance();
        deserializer = NofspDeserializer.getInstance();
        this.running = false;
    }

    /**
     * Runs the server.
     */
    public synchronized void run() {
        if (running) {
            throw new IllegalStateException("Cannot run server, because server is already running.");
        }

        serverSocket = openListeningSocket();

        if (serverSocket != null) {
            running = true;

            Thread listeningThread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = acceptNextClient();

                    if (clientSocket != null) {
                        ClientHandler clientHandler = new ClientHandler(clientSocket, serializer, deserializer);
                        clientHandler.run();
                    }
                }

                running = false;
            });

            listeningThread.start();
        }
    }

    /**
     * Stops the server.
     */
    public synchronized void stop() {
        if (!running) {
            throw new IllegalStateException("Cannot stop server, because it is not currently running.");
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                running = false;
            } else {
                Logger.error("Cannot stop server: " + e.getMessage());
            }
        }

        running = false;
    }

    /**
     * Accepts the next incoming client connection, and returns the associated socket.
     *
     * @return the client socket
     */
    private Socket acceptNextClient() {
        Socket clientSocket = null;

        try {
            clientSocket = serverSocket.accept();
            Logger.info("Client " + clientSocket.getRemoteSocketAddress() + " has connected to the server.");
        } catch (IOException e) {
            Logger.error("Cannot accept next client: " + e.getMessage());
        }

        return clientSocket;
    }

    /**
     * Establishes the listening server socket and returns it.
     *
     * @return the server socket
     */
    private ServerSocket openListeningSocket() {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(60005);
            Logger.info("Server listening on port " + PORT_NUMBER + "...");
        } catch (IOException e) {
            Logger.error("Cannot open server socket: " + e.getMessage());
        }

        return socket;
    }
}
