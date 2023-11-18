package no.ntnu.network.connectionservice;

/**
 * A listener listening for shutdowns of connection services.
 * Connection services can be crucial for a connection to be properly maintained, and connection maintaining classes
 * can use this interface to handle the event of a shutdown of such a service.
 */
public interface ConnServiceShutdownListener {
    /**
     * Triggers when a {@code ConnectionService} shuts down.
     *
     * @param service the shutdown connection service
     */
    void connectionServiceShutdown(ConnectionService service);
}
