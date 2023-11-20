package no.ntnu.network.client;

import no.ntnu.network.ClientAgent;
import no.ntnu.network.ControlProcessAgent;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.ClientLogger;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * A client communication agent, responsible for communicating with a server.
 */
public abstract class Client<C extends MessageContext> extends ControlProcessAgent<C> implements ClientAgent {
    private int nodeAddress;

    /**
     * Creates a new Client.
     */
    protected Client() {
        super();
        nodeAddress = -1;
    }

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
                                      ByteSerializerVisitor serializer, MessageDeserializer<C> deserializer) {
        boolean success = false;

        try {
            setSocket(new Socket(serverAddress, portNumber));
            if (establishConnection(serializer, deserializer)) {
                success = true;
                Logger.info("Successfully connected to server " + getRemoteSocketAddress());
            }
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

    /**
     * Returns the address for the node.
     *
     * @return the node address
     */
    public int getNodeAddress() {
        return nodeAddress;
    }

    @Override
    public void setClientNodeAddress(int address) {
        this.nodeAddress = address;
    }

    @Override
    protected void handleEndOfMessageStream() {
        Logger.error("End of the message stream has been met, and the connection will therefore be closed.");
    }

    @Override
    protected void handleMessageReadingException(IOException e) {
        Logger.error("An exception has been encountered while reading messages and the connection" +
                "will therefore be closed: " + e.getMessage());
    }

    @Override
    protected void logDisconnection() {
        Logger.info("Disconnected from the server.");
    }


    @Override
    public void requestTimedOut(RequestMessage requestMessage) {
        ClientLogger.requestTimeout(requestMessage);
    }

    @Override
    protected void logSendRequestMessage(RequestMessage request) {
        ClientLogger.requestSent(request);
    }

    @Override
    protected void logSendResponseMessage(ResponseMessage response) {
        ClientLogger.responseSent(response);
    }
}
