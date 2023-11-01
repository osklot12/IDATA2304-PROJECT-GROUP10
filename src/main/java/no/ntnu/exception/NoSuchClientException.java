package no.ntnu.exception;

/**
 * An exception thrown when no client with a given property exists.
 */
public class NoSuchClientException extends RuntimeException {
    /**
     * Creates a new NoSuchDeviceException.
     *
     * @param message description of exception
     */
    public NoSuchClientException(String message) {
        super(message);
    }

    /**
     * Creates a new NoSuchDeviceException.
     */
    public NoSuchClientException() {
        super();
    }
}
