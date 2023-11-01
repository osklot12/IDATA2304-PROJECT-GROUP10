package no.ntnu.broker;

import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.controlpanel.virtual.VirtualFieldNodeListener;
import no.ntnu.controlpanel.virtual.VirtualSDUSensor;

/**
 * A class representing a broker for events triggered by a virtual field node.
 */
public class VirtualFieldNodeBroker extends SubscriberList<VirtualFieldNodeListener> {
    /**
     * Creates a new VirtualFieldNodeBroker.
     */
    public VirtualFieldNodeBroker() {
        super();
    }

    /**
     * Notifies the subscribed listeners that the state for a virtual actuator has changed.
     *
     * @param actuatorAddress the address for the virtual actuator
     * @param actuator the virtual actuator
     * @param global true if change is to be notified as a global change, false if change is local
     */
    public void notifyActuatorStateChanged(VirtualFieldNode fieldNode, int actuatorAddress, boolean global) {
        getSubscribers().forEach(
                (listener) -> {
                    listener.virtualActuatorStateChanged(fieldNode, actuatorAddress, global);
                }
        );
    }

    /**
     * Notifies the subscribed listeners that new SDU data has been captured.
     *
     * @param sensor the virtual sensor that captured the data
     */
    public void notifyNewSDUData(VirtualSDUSensor sensor) {
        getSubscribers().forEach(
                (listener) -> {
                    listener.newSDUData(sensor);
                }
        );
    }
}
