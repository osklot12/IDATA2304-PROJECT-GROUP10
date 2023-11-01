package no.ntnu.network.centralserver;

import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

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
    private ServerSocket serverSocket;
    private boolean running;


    /**
     * Creates a new CentralServer.
     */
    public CentralServer() {
        running = false;
    }

    /**
     * Runs the server.
     */
    public void run() {
        serverSocket = openListeningSocket();

        if (serverSocket != null) {
            running = true;

            while (running) {
                Socket clientSocket = acceptNextClient();

                if (clientSocket != null) {
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.run();
                }
            }
        }
    }

    private Socket acceptNextClient() {
        Socket clientSocket = null;

        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            Logger.error("Cannot accept next client: " + e.getMessage());
        }

        return clientSocket;
    }

    private ServerSocket openListeningSocket() {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(60005);
            Logger.info("Server running on port " + PORT_NUMBER);
        } catch (IOException e) {
            Logger.error("Cannot open server socket: " + e.getMessage());
        }

        return socket;
    }
}
