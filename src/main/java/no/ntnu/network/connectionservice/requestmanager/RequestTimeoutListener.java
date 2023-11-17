package no.ntnu.network.connectionservice.requestmanager;

import no.ntnu.network.message.request.RequestMessage;

/**
 * A listener listening for request message timeouts.
 */
public interface RequestTimeoutListener {
    /**
     * Triggers when a pending request has timed out.
     *
     * @param requestMessage the timed out request message
     */
    void requestTimedOut(RequestMessage requestMessage);
}
