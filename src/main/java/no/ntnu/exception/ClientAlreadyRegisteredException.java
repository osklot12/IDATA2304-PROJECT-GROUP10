package no.ntnu.exception;

/**
 * An exception thrown when an already registered client tries to register.
 */
public class ClientAlreadyRegisteredException extends RuntimeException {
    /**
     * Creates a new ClientAlreadyRegisteredException.
     *
     * @param message description of exception
     */
    public ClientAlreadyRegisteredException(String message) {
        super(message);
    }

    /**
     * Creates a new ClientAlreadyRegisteredException.
     */
    public ClientAlreadyRegisteredException() {
        super();
    }
}
