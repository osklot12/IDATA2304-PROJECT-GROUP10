package no.ntnu.broker;

import no.ntnu.network.connectionservice.requestmanager.RequestTimeoutListener;
import no.ntnu.network.message.request.RequestMessage;

/**
 * A broker for notifying listeners about the timeout for a request.
 */
public class RequestTimeoutBroker extends SubscriberList<RequestTimeoutListener> {
    /**
     * Notifies the listeners about a request timeout.
     *
     * @param request the timed out request
     */
    public void notifyListeners(RequestMessage request) {
        getSubscribers().forEach(listener -> listener.requestTimedOut(request));
    }
}
