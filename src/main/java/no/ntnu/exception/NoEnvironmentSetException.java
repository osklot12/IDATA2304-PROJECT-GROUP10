package no.ntnu.exception;

/**
 * A class representing an exception for when no environment is set for a given device.
 */
public class NoEnvironmentSetException extends RuntimeException {
    /**
     * Creates a new NoEnvironmentSetException.
     *
     * @param message description of the exception
     */
    public NoEnvironmentSetException(String message) {
        super(message);
    }

    /**
     * Creates a new NoEnvironmentSetException.
     */
    public NoEnvironmentSetException() {
        super();
    }
}
