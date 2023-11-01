package no.ntnu.exception;

/**
 * An exception thrown for when no actuator with a given address is found.
 */
public class NoSuchActuatorException extends RuntimeException {
    /**
     * Creates a new NoSuchActuatorException.
     *
     * @param message description of the exception
     */
    public NoSuchActuatorException(String message) {
        super(message);
    }

    /**
     * Creates a new NoSuchActuatorException.
     */
    public NoSuchActuatorException() {
        super();
    }
}
