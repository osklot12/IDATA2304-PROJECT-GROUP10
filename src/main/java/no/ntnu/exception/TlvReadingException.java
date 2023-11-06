package no.ntnu.exception;

/**
 * An exception thrown when a Tlv cannot be read.
 */
public class TlvReadingException extends RuntimeException{
    /**
     * Creates a new TlvReadingException.
     *
     * @param message description of exception
     */
    public TlvReadingException(String message) {
        super(message);
    }
}
