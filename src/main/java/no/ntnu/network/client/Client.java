package no.ntnu.network.client;

import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A client for communicating with a server.
 */
public abstract class Client extends CommunicationAgent {
    /**
     * Connects the client to a server.
     *
     * @param serverAddress the ip address of the server
     * @param portNumber the port number for the server
     * @param serializer the serializer used to serialize messages
     * @param deserializer the deserializer used to deserialize messages
     * @return true if successfully connected to server, false otherwise
     */
    protected boolean connectToServer(String serverAddress, int portNumber,
                                      ByteSerializerVisitor serializer, ByteDeserializer deserializer) {
        boolean success = false;

        try {
            Socket socket = new Socket(serverAddress, portNumber);
            success = establishConnection(socket, serializer, deserializer);
        } catch (IOException e) {
            Logger.error("Cannot connect to server '" + serverAddress + "' with port number " + portNumber +
                    ": " + e.getMessage());
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
