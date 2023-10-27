package no.ntnu.broker;

import no.ntnu.fieldnode.device.actuator.Actuator;
import no.ntnu.fieldnode.device.actuator.ActuatorListener;

/**
 * A class representing a broker responsible for notifying ActuatorListeners about a change
 * of state for an actuator.
 */
public class ActuatorStateBroker extends SubscriberList<ActuatorListener> {
    /**
     * Creates a new ActuatorStateBroker.
     */
    public ActuatorStateBroker() {
        super();
    }

    /**
     * Notifies the subscribed listeners about a change of state for an actuator.
     *
     * @param actuator the actuator that changed state
     */
    public void notifyListeners(Actuator actuator) {
        getSubscribers().forEach(
                (listener) -> {
                    listener.actuatorStateChanged(actuator);
                }
        );
    }
}
