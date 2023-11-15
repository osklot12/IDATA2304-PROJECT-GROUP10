package no.ntnu.network.message.context;

import no.ntnu.network.message.response.ResponseMessage;

/**
 * A context that messages can operate on.
 */
public interface MessageContext {
    /**
     * Responds to the remote peer.
     *
     * @param response the response message
     */
    void respond(ResponseMessage response);

    /**
     * Accepts a response message.
     *
     * @return true if accepted, false otherwise
     */
    boolean acceptResponse(ResponseMessage response);
}
