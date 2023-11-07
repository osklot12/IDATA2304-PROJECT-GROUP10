package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;

/**
 * A message requesting for a service.
 */
public interface Request extends Message {
    /**
     * Returns the command for the request.
     *
     * @return the command
     */
    String getCommand();
}
