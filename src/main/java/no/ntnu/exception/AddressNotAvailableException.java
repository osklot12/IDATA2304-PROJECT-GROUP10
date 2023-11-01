package no.ntnu.exception;

/**
 * An exception thrown when a particular address is not available for claiming.
 */
public class AddressNotAvailableException extends RuntimeException {
    /**
     * Creates a new AddressNotAvailableException.
     *
     * @param message description of the exception
     */
    public AddressNotAvailableException(String message) {
        super(message);
    }

    /**
     * Creates a new AddressNotAvailableException.
     */
    public AddressNotAvailableException() {
        super();
    }
}
