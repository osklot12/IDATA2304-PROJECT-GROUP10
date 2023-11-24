package no.ntnu.broker;

import no.ntnu.fieldnode.device.actuator.ActuatorListener;

import java.util.HashMap;

/**
 * A class representing a broker responsible for notifying ActuatorListeners about a change
 * of state for an actuator. The broker stores the listeners together with their address for the actuator,
 * as different listeners can have different addresses for the same actuator.
 */
public class ActuatorStateBroker extends HashMap<ActuatorListener, Integer> {
    /**
     * Notifies all listeners about the event of a changed actuator state.
     *
     * @param newState the new state of the actuator
     */
    public void notifyListeners(int newState) {
        forEach((listener, address) -> listener.actuatorStateChanged(address, newState));
    }
}
