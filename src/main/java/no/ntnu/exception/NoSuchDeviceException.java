package no.ntnu.exception;

/**
 * An exception thrown when no device with a given property exists.
 */
public class NoSuchDeviceException extends RuntimeException {
    /**
     * Creates a new NoSuchDeviceException.
     *
     * @param message description of exception
     */
    public NoSuchDeviceException(String message) {
        super(message);
    }

    /**
     * Creates a new NoSuchDeviceException.
     */
    public NoSuchDeviceException() {
        super();
    }
}
