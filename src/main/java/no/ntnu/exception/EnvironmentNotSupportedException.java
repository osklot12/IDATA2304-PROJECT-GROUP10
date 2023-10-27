package no.ntnu.exception;

/**
 * A class representing an exception for
 */
public class EnvironmentNotSupportedException extends RuntimeException {
    /**
     * Creates a new EnvironmentNotSupportedException.
     *
     * @param message description of the exception
     */
    public EnvironmentNotSupportedException(String message) {
        super(message);
    }

    /**
     * Creates a new EnvironmentNotSupportedException.
     */
    public EnvironmentNotSupportedException() {
        super();
    }
}
