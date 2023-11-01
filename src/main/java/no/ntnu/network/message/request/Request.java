package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;

/**
 * A request from an entity in the network to another.
 */
public interface Request {
    /**
     * Executes the request and generates an appropriate response.
     *
     * @return a response to the request
     */
    Message execute();
}
