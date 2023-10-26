package no.ntnu.exception;

/**
 * An exception thrown for invalid actuator states.
 */
public class ActuatorInvalidStateException extends RuntimeException {
    /**
     * Creates a new ActuatorInvalidStateException.
     * @param message description of the exception
     */
    public ActuatorInvalidStateException(String message) {
        super(message);
    }

    /**
     * Creates a new ActuatorInvalidStateException.
     */
    public ActuatorInvalidStateException() {
        super();
    }
}
