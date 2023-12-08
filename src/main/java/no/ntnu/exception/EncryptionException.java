package no.ntnu.exception;

/**
 * An exception thrown when encryption/decryption fails.
 */
public class EncryptionException extends Exception {
    /**
     * Creates a new EncryptionException.
     *
     * @param message the description of the exception
     */
    public EncryptionException(String message) {
        super(message);
    }
}
