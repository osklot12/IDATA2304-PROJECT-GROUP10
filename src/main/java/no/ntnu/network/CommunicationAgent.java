package no.ntnu.network;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * An agent responsible for communication with another peer.
 */
public interface CommunicationAgent {
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
     * Returns whether a response is accepted or not.
     *
     * @param responseMessage the response message to accept
     * @return true if accepted, false otherwise
     */
    boolean acceptResponse(ResponseMessage responseMessage);

    /**
     * Returns a string representation of the remote entity.
     *
     * @return string representation of remote entity
     */
    String getRemoteEntityAsString();

    /**
     * Closes the connection.
     */
    void close();
}
