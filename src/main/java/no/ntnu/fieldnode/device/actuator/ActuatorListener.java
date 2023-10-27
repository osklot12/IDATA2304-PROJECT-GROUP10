package no.ntnu.fieldnode.device.actuator;

/**
 * A listener listening for a change of an actuator state.
 */
public interface ActuatorListener {
    /**
     * Triggers when a state for an actuator changes.
     *
     * @param actuator actuator that changes state
     */
    void actuatorStateChanged(Actuator actuator);
}
