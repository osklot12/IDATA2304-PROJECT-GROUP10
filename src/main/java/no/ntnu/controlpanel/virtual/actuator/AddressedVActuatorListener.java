package no.ntnu.controlpanel.virtual.actuator;

/**
 * A listener listening for a change of state for a virtual actuator.
 * The event is associated with an address, which the listener may use to identify the actuator.
 */
public interface AddressedVActuatorListener {
    /**
     * Triggers when the state of an actuator changes.
     *
     * @param actuatorAddress the address of the actuator
     */
    void virtualActuatorStateChanged(int actuatorAddress);
}
