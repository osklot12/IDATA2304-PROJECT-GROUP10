package no.ntnu.network.message.context;

import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A context that can be used by messages for processing.
 * The MessageContext provides the message with necessary methods for processing, and helps to encapsulate the means
 * for message processing.
 */
public interface MessageContext {
    /**
     * Responds to the remote peer.
     * Used when processing a request.
     *
     * @param response the response message
     * @throws IOException thrown if an I/O exception is thrown
     */
    void respond(ResponseMessage response) throws IOException;

    /**
     * Accepts a response message.
     * Used when processing a response.
     *
     * @return true if accepted, false otherwise
     */
    boolean acceptResponse(ResponseMessage response);

    /**
     * Logs the receiving of a request message.
     *
     * @param request the received request
     */
    void logReceivingRequest(RequestMessage request);

    /**
     * Logs the receiving of a response message.
     *
     * @param response the received response
     */
    void logReceivingResponse(ResponseMessage response);
}
