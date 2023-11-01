package no.ntnu.network.centralserver;

import no.ntnu.tools.Logger;

import java.io.*;
import java.net.Socket;

/**
 * A class responsible for handling all communication with a client.
 */
public class ClientHandler {
    private final Socket clientSocket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    /**
     * Creates a new ClientHandler.
     *
     * @param clientSocket the client to handle
     */
    public ClientHandler(Socket clientSocket) {
        if (clientSocket == null) {
            throw new IllegalArgumentException("Cannot create ClientHandler, because client socket is null.");
        }

        this.clientSocket = clientSocket;
    }

    /**
     * Runs the ClientHandler.
     */
    public void run() {
        if (establishSocketStreams()) {

        }
    }

    private boolean establishSocketStreams() {
        socketReader = establishSocketReader(clientSocket);
        socketWriter = establishSocketWriter(clientSocket);

        return (socketReader != null) && (socketWriter != null);
    }

    private PrintWriter establishSocketWriter(Socket clientSocket) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            Logger.error("Cannot establish output stream for client: " + e.getMessage());
        }

        return writer;
    }

    private BufferedReader establishSocketReader(Socket clientSocket) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            Logger.error("Cannot establish input stream for client:" + e.getMessage());
        }

        return reader;
    }
}
