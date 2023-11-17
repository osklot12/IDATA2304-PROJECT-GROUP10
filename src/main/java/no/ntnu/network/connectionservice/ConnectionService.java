package no.ntnu.network.connectionservice;

/**
 * A service working for a TCP connection, performing some task only while the connection is open.
 */
public interface ConnectionService {
    /**
     * Starts the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();
}
