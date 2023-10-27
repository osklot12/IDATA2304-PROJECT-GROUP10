package no.ntnu.broker;

import no.ntnu.fieldnode.device.sensor.Sensor;
import no.ntnu.fieldnode.device.sensor.SensorListener;


/**
 * A class representing a broker responsible for notifying SensorListeners about a capture of some new sensor data.
 */
public class SensorDataBroker extends SubscriberList<SensorListener> {
    /**
     * Creates a new SensorDataBroker.
     */
    public SensorDataBroker() {
        super();
    }

    /**
     * Notifies the subscribed listeners about new data capture for a sensor.
     *
     * @param sensor the sensor that has captured new data
     */
    public void notifyListeners(Sensor sensor) {
        getSubscribers().forEach(
                (listener) -> {
                    listener.sensorDataCaptured(sensor);
                }
        );
    }
}
