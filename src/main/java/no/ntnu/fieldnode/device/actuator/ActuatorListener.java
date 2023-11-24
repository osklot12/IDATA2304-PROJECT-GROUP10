package no.ntnu.fieldnode.device.actuator;

/**
 * A listener listening for a change of an actuator state.
 */
public interface ActuatorListener {
    /**
     * Triggers when a state for an actuator changes.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    void actuatorStateChanged(int actuatorAddress, int newState);
}
