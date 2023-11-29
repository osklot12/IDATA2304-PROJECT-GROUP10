package no.ntnu.network.client;

import no.ntnu.network.ControlProcessAgent;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.logger.ClientLogger;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A client communication agent, responsible for communicating with a server.
 */
public abstract class Client<C extends ClientContext> extends ControlProcessAgent<C> {
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
     * Returns the InetAddress for the server.
     *
     * @return inet address for the server
     */
    protected InetAddress getServerInetAddress() {
        if (socket == null) {
            throw new IllegalStateException("Cannot get server inet address, because socket is not established.");
        }

        return socket.getInetAddress();
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
