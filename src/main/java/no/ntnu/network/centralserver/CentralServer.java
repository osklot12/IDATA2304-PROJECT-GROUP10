package no.ntnu.network.centralserver;

import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.connectionservice.sensordatarouter.UdpSensorDataRouter;
import no.ntnu.network.message.deserialize.NofspServerDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.sensordataprocess.UdpSensorDataSink;
import no.ntnu.tools.logger.SimpleLogger;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

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
    private UdpSensorDataRouter sensorDataRouter;
    private final Set<SimpleLogger> loggers;

    /**
     * Creates a new CentralServer.
     */
    public CentralServer() {
        this.centralHub = new CentralHub();
        this.serializer = new NofspSerializer();
        this.deserializer = new NofspServerDeserializer(centralHub);
        this.running = false;
        this.loggers = new HashSet<>();
    }

    /**
     * Adds a logger to log central server related events.
     *
     * @param logger the logger to add
     */
    public void addLogger(SimpleLogger logger) {
        loggers.add(logger);
        centralHub.addLogger(logger);
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
            logInfo("Server listening for incoming UDP sensor data messages on port " + DATA_PORT_NUMBER + "...");
            if (startHandlingIncomingClients()) {
                logInfo("Server listening for incoming client TPC connections on port " + CONTROL_PORT_NUMBER + "...");
            }
        }
    }

    /**
     * Logs info.
     *
     * @param message the information to log
     */
    private void logInfo(String message) {
        loggers.forEach(logger -> logger.logInfo(message));
    }

    /**
     * Logs an error.
     *
     * @param error the error message to log
     */
    private void logError(String error) {
        loggers.forEach(logger -> logger.logError(error));
    }

    /**
     * Starts handling incoming TCP client connections.
     *
     * @return true if successfully listening for incoming clients
     */
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
                        // passes the loggers to the client handler to log client specific events
                        loggers.forEach(clientHandler::addLogger);
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

    /**
     * Starts handling incoming UDP sensor data.
     *
     * @return true if successfully listening for sensor data
     */
    private boolean startHandlingIncomingSensorData() {
        boolean success = false;

        try {
            UdpSensorDataSink sensorDataSink = new UdpSensorDataSink(deserializer, DATA_PORT_NUMBER);
            sensorDataRouter = new UdpSensorDataRouter(sensorDataSink);
            sensorDataRouter.addDestination(centralHub);
            sensorDataRouter.start();
            success = true;
        } catch (SocketException e) {
            logError("Could not start handling incoming sensor data: " + e.getMessage());
        }

        return success;
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
            sensorDataRouter.stop();
            logInfo("Server has been shut down.");
        } catch (IOException e) {
            if (serverSocket.isClosed()) {
                running = false;
            } else {
                logError("Cannot stop server: " + e.getMessage());
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
            logInfo("Client " + clientSocket.getRemoteSocketAddress() + " has requested to connect to the server.");
        } catch (IOException e) {
            logError("Cannot accept next client: " + e.getMessage());
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
            logError("Cannot open server socket: " + e.getMessage());
        }

        return socket;
    }
}
