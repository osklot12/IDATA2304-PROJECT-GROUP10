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

    /**
     * Notifies the listeners about the capture of SDU sensor data.
     *
     * @param sensorAddress the sensor that captured the data
     * @param data the sdu data
     */
    public void notifySduSensorDataCapture(int sensorAddress, double data) {
        getSubscribers().forEach(subscriber -> subscriber.sensorDataCapture(sensorAddress, data));
    }
}
