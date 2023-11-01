package no.ntnu.controlpanel.virtual;

/**
 * A listener listening for a change of state for a virtual actuator.
 */
public interface VirtualActuatorListener {
    /**
     * Triggers when the state of a virtual actuator changes.
     *
     * @param actuator the virtual actuator that changes state
     * @param global true if change is global, false if change is local
     */
    void virtualActuatorStateChanged(VirtualStandardActuator actuator, boolean global);
}
