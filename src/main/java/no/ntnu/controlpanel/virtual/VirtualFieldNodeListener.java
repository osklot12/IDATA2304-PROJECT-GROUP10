package no.ntnu.controlpanel.virtual;

/**
 * A listener listening for a change of a virtual field node.
 */
public interface VirtualFieldNodeListener {
    /**
     * Triggers when the virtual field node registers a change of state for a virtual actuator.
     *
     * @param fieldNode the virtual field node that triggered the event
     * @param actuatorAddress the address of the virtual actuator
     * @param global true if change is to be treated as global, false if change is local
     */
    void virtualActuatorStateChanged(VirtualFieldNode fieldNode, int actuatorAddress, boolean global);

    /**
     * Triggers when the virtual field node registers new data for a virtual sensor.
     *
     * @param sensor the virtual sensor that stored the data
     */
    void newSDUData(VirtualSDUSensor sensor);
}
