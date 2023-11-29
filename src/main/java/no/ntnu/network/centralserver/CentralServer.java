package no.ntnu.network.centralserver;

import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.deserialize.NofspServerDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.sensordataprocess.UdpDatagramReceiver;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.net.*;

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
    public static final int CONTROL_PORT_NUMBER = 60005;
    public static final int DATA_PORT_NUMBER = 60006;
    private final CentralHub centralHub;
    private final ByteSerializerVisitor serializer;
    private final NofspServerDeserializer deserializer;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private UdpDatagramReceiver datagramReceiver;

    /**
     * Creates a new CentralServer.
     */
    public CentralServer() {
        this.centralHub = new CentralHub();
        this.serializer = new NofspSerializer();
        this.deserializer = new NofspServerDeserializer(centralHub);
        this.running = false;
    }

    /**
     * Runs the server.
     */
    public synchronized void run() {
        if (running) {
            throw new IllegalStateException("Cannot run server, because server is already running.");
        }

        running = true;
        if (startHandlingIncomingSensorData()) {
                Logger.info("Server listening for incoming UDP sensor data messages on port " + DATA_PORT_NUMBER + "...");
            if (startHandlingIncomingClients()) {
                Logger.info("Server listening for incoming client TPC connections on port " + CONTROL_PORT_NUMBER + "...");
            }
        }
    }

    private boolean startHandlingIncomingClients() {
        boolean success = false;

        serverSocket = openListeningSocket();

        if (serverSocket != null) {
            success = true;
            Thread incomingClientListeningThread = new Thread(() -> {
                while (running) {
                    Socket clientSocket = acceptNextClient();

                    if (clientSocket != null) {
                        ClientHandler clientHandler = new ClientHandler(clientSocket, centralHub, serializer, deserializer);
                        clientHandler.run();
                    }
                }

                running = false;
            });

            incomingClientListeningThread.start();
        } else {
            stop();
        }

        return success;
    }

    private boolean startHandlingIncomingSensorData() {
        boolean success = false;

        if (establishUdpMessageReceiver()) {
            Thread datagramReceivingThread = new Thread(() -> {
                while (running) {
                    handleNextReceivedDatagram();
                }
            });

            datagramReceivingThread.start();
            success = true;
        } else {
            stop();
        }

        return success;
    }

    private boolean establishUdpMessageReceiver() {
        boolean success = false;

        try {
            DatagramSocket datagramSocket = new DatagramSocket(DATA_PORT_NUMBER);
            datagramReceiver = new UdpDatagramReceiver(datagramSocket, 1024);
            success = true;
        } catch (IOException e) {
            Logger.error("Could not establish datagram socket: " + e.getMessage());
        }

        return success;
    }

    private void handleNextReceivedDatagram() {
        try {
            DatagramPacket receivedDatagram = datagramReceiver.getNextDatagramPacket();
            if (receivedDatagram != null) {
                ServerDatagramHandler datagramHandler = new ServerDatagramHandler(receivedDatagram, centralHub, deserializer);
                datagramHandler.run();
            }
        } catch (IOException e) {
            Logger.error("Could not receive datagram packet: " + e.getMessage());
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
            running = false;
            serverSocket.close();
            Logger.info("Server has been shut down.");
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                running = false;
            } else {
                Logger.error("Cannot stop server: " + e.getMessage());
            }
        }
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
            Logger.info("Client " + clientSocket.getRemoteSocketAddress() + " has requested to connect to the server.");
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
        } catch (IOException e) {
            Logger.error("Cannot open server socket: " + e.getMessage());
        }

        return socket;
    }
}
