package no.ntnu.network;

import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.response.ResponseMessage;

/**
 * An agent responsible for communication with another peer.
 */
public interface CommunicationAgent {
    /**
     * Sends a control message to the remote peer.
     *
     * @param message message to send
     */
    void sendMessage(ControlMessage message);

    /**
     * Accepts a response message.
     *
     * @param responseMessage the response message to accept
     * @return true if accepted, false otherwise
     */
    boolean acceptResponse(ResponseMessage responseMessage);

    /**
     * Closes the connection.
     */
    void close();
}
