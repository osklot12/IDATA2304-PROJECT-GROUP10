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
     * @param service the shutdown connection service
     */
    public void notifyListeners(ConnectionService service) {
        getSubscribers().forEach(listener -> listener.connectionServiceShutdown(service));
    }
}
