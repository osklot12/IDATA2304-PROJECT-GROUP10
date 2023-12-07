package no.ntnu.controlpanel.virtual;

/**
 * A listener listening for a change of a virtual field node.
 */
public interface VirtualFieldNodeListener {
    /**
     * Triggers when the virtual field node registers a change of state for a virtual actuator.
     *
     * @param fieldNodeAddress the address of the virtual field node
     * @param actuatorAddress the address of the virtual actuator
     */
    void virtualActuatorStateChanged(int fieldNodeAddress, int actuatorAddress);
}
