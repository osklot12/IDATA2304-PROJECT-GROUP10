package no.ntnu.exception;

/**
 * An exception thrown when a server connection is required, but not established.
 */
public class NoServerConnectionException extends RuntimeException {
    /**
     * Creates a new NoServerConnectionException.
     *
     * @param message description of exception
     */
    public NoServerConnectionException(String message) {
        super(message);
    }
}
