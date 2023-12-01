package no.ntnu.network;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * An agent responsible for communicating control messages with another entity in the network.
 * The interface decouples the concrete implementations of data communication from the other parts of the application.
 */
public interface ControlCommAgent {
    /**
     * Sends a request message to the remote peer.
     *
     * @param request request message to send
     * @throws IOException thrown if an I/O exception is thrown
     */
    void sendRequest(RequestMessage request) throws IOException;

    /**
     * Sends a response message to the remote peer.
     *
     * @param response response message to send
     * @throws IOException thrown if an I/O exception is thrown
     */
    void sendResponse(ResponseMessage response) throws IOException;

    /**
     * Alerts the agent that a response has been received, returning the request associated with a response.
     *
     * @param responseMessage the response message to accept
     * @return the related request message, null if no associated request exists
     */
    RequestMessage acceptResponse(ResponseMessage responseMessage);

    /**
     * Returns a string representation of the remote entity.
     *
     * @return string representation of remote entity
     */
    String getRemoteEntityAsString();

    /**
     * Returns the node address for the associated client.
     * On the client side, the value returned is its own client node address.
     * On the server side however, the value returned is the node address for the remote client.
     *
     * @return the address for the associated client, -1 if client is not registered
     */
    int getClientNodeAddress();

    /**
     * Sets the node address for the client agent.
     * On the client side, the value set is its own client node address.
     * On the server side however, the value set is the client node address for the remote client.
     *
     * @param address the client node address
     */
    void setClientNodeAddress(int address);

    /**
     * Closes the connection.
     */
    void close();
}
