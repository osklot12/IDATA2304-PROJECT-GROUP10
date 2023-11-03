package no.ntnu.exception;

/**
 * An exception thrown when a message cannot be processed.
 */
public class MessageProcessException extends RuntimeException {
    /**
     * Creates a new MessageProcessException.
     *
     * @param message description of exception
     */
    public MessageProcessException(String message) {
        super(message);
    }
}
