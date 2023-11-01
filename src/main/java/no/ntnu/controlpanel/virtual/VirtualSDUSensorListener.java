package no.ntnu.controlpanel.virtual;

/**
 * A listener listening for a change in data held by a virtual sdu sensor.
 */
public interface VirtualSDUSensorListener {
    /**
     * Triggers when new sdu data is stored by a virtual sdu sensor.
     *
     * @param virtualSDUSensor virtual sdu sensor that triggered the event
     */
    void newSDUData(VirtualSDUSensor virtualSDUSensor);
}
