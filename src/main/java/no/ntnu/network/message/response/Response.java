package no.ntnu.network.message.response;

import no.ntnu.network.message.Message;

/**
 * A response to a {@code Request} message.
 */
public interface Response extends Message {
    /**
     * Returns the status code for the response.
     *
     * @return the status code
     */
    int getStatusCode();
}
