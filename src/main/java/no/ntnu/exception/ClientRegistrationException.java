package no.ntnu.exception;

/**
 * An exception thrown when a client cannot be registered.
 */
public class ClientRegistrationException extends RuntimeException {
    /**
     * Creates a new ClientRegistrationException.
     *
     * @param message description of exception
     */
    public ClientRegistrationException(String message) {
        super(message);
    }
}
