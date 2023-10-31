package no.ntnu.exception;

/**
 * A class representing an exception thrown when trying to access a virtual device which does not exist.
 */
public class NoSuchVirtualDeviceException extends RuntimeException {
    /**
     * Creates a new NoSuchVirtualDeviceException.
     *
     * @param message description of exception
     */
    public NoSuchVirtualDeviceException(String message) {
        super(message);
    }

    /**
     * Creates a new NoSuchVirtualDeviceException.
     */
    public NoSuchVirtualDeviceException() {
        super();
    }
}
