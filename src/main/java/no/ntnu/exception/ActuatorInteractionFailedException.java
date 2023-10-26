package no.ntnu.exception;

/**
 * A class representing an exception for a failed interaction with an actuator.
 */
public class ActuatorInteractionFailedException extends RuntimeException {
    /**
     * Creates an ActuatorInteractionFailedException.
     *
     * @param message description of the exception
     */
    public ActuatorInteractionFailedException(String message) {
        super(message);
    }

    /**
     * Creates an ActuatorInteractionFailedException.
     */
    public ActuatorInteractionFailedException() {
        super();
    }
}
