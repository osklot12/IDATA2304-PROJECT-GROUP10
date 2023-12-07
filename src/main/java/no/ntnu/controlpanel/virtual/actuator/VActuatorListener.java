package no.ntnu.controlpanel.virtual.actuator;

/**
 * A listener listening for a change of state for a virtual actuator.
 */
public interface VActuatorListener {
    /**
     * Triggers when the state of a virtual actuator changes.
     */
    void virtualActuatorStateChanged();

    /**
     * Returns the destination for the notification.
     *
     * @return the destination
     */
    Object getVActuatorEventDestination();
}
