package no.ntnu.exception;

/**
 * An exception thrown when encountering errors while serializing or deserializing.
 */
public class SerializationException extends RuntimeException {
    /**
     * Creates a SerializationException.
     *
     * @param message description of exception
     */
    public SerializationException(String message) {
        super(message);
    }
}
