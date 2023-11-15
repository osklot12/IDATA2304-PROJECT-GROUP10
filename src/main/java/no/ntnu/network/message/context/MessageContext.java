package no.ntnu.network.message.context;

import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A context that messages can operate on.
 */
public interface MessageContext {
    /**
     * Responds to the remote peer.
     *
     * @param response the response message
     * @throws IOException thrown if an I/O exception is thrown
     */
    void respond(ResponseMessage response) throws IOException;

    /**
     * Accepts a response message.
     *
     * @return true if accepted, false otherwise
     */
    boolean acceptResponse(ResponseMessage response);
}
