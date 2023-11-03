package no.ntnu.network.centralserver;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A class responsible for handling all communication with a client.
 */
public class ClientHandler {
    private final Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;


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
        if (establishStreams()) {

        } else {
            Logger.error("Cannot establish streams for client socket.");
        }
    }

    private boolean establishStreams() {
        boolean success = false;

        inputStream = establishInputStream();

        if (inputStream != null) {
            outputStream = establishOutputStream();

            if (outputStream != null) {
                success = true;
            }
        }

        return success;
    }

    private OutputStream establishOutputStream() {
        OutputStream stream = null;

        try {
            stream = clientSocket.getOutputStream();
        } catch (IOException e) {
            Logger.error("Cannot establish output stream for client socket.");
        }

        return stream;
    }

    private InputStream establishInputStream() {
        InputStream stream = null;

        try {
            stream = clientSocket.getInputStream();
        } catch (IOException e) {
            Logger.error("Cannot establish input stream for client socket.");
        }

        return stream;
    }

    private Message readClientMessage() {
        Message clientMessage = null;



        return clientMessage;
    }
}
