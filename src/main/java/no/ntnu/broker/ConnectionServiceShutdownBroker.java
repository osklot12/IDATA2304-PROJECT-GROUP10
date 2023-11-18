package no.ntnu.broker;

import no.ntnu.network.connectionservice.ConnectionService;
import no.ntnu.network.connectionservice.ConnServiceShutdownListener;

/**
 * A broker for notifying listeners about the shutdown of a {@code ConnectionService}.
 */
public class ConnectionServiceShutdownBroker extends SubscriberList<ConnServiceShutdownListener> {
    /**
     * Notifies the listeners about the shutdown of a connection service.
     *
     * @param message the description of the shutdown
     */
    public void notifyListeners(String message) {
        getSubscribers().forEach(listener -> listener.connectionServiceShutdown(message));
    }
}
