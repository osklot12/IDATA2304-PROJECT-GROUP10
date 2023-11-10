package no.ntnu.exception;

import java.io.IOException;

/**
 * An exception thrown when encountering errors while serializing or deserializing.
 */
public class SerializationException extends IOException {
    /**
     * Creates a SerializationException.
     *
     * @param message description of exception
     */
    public SerializationException(String message) {
        super(message);
    }
}
