package no.ntnu.broker;

import no.ntnu.fieldnode.FieldNodeListener;

/**
 * A broker for notifying listeners about events occurring for a field node.
 */
public class FieldNodeEventBroker extends SubscriberList<FieldNodeListener> {
    /**
     * Notifies the listeners about the change of state of an actuator.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    public void notifyActuatorStateChange(int actuatorAddress, int newState) {
        getSubscribers().forEach(subscriber -> subscriber.actuatorStateChange(actuatorAddress, newState));
    }

    public void notifySensorDataCapture(int sensorAddress, double data) {

    }
}
