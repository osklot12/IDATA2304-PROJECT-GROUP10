package no.ntnu.exception;

/**
 * An exception thrown when a given address does not exist.
 */
public class NoSuchAddressException extends RuntimeException {
    /**
     * Creates a new NoSuchAddressException.
     *
     * @param message the exception description
     */
    public NoSuchAddressException(String message) {
        super(message);
    }
}
