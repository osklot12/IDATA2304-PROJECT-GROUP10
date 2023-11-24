package no.ntnu.exception;

/**
 * An exception thrown when subscribing to another entity fails.
 */
public class SubscriptionException extends Exception {
    /**
     * Creates a new SubscriptionException.
     *
     * @param message the exception description
     */
    public SubscriptionException(String message) {
        super(message);
    }
}
