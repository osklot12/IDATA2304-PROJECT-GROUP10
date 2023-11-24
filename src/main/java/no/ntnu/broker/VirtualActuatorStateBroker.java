package no.ntnu.broker;

import no.ntnu.controlpanel.virtual.VirtualActuatorListener;
import no.ntnu.controlpanel.virtual.VirtualStandardActuator;

/**
 * A class representing a broker for events triggered by virtual actuators.
 */
public class VirtualActuatorStateBroker extends SubscriberList<VirtualActuatorListener> {
    /**
     * Notifies the subscribed listeners about a change of state for a virtual actuator.
     *
     * @param actuator the actuator that changed state
     */
    public void notifyListeners(VirtualStandardActuator actuator, boolean global) {
        getSubscribers().forEach(listener -> listener.virtualActuatorStateChanged(actuator, global)
        );
    }
}
