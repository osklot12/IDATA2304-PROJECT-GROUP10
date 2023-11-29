package no.ntnu.broker;

import no.ntnu.fieldnode.device.sensor.SduSensorListener;

import java.util.HashMap;


/**
 * A class representing a broker responsible for notifying SensorListeners about a capture of some new sensor data.
 */
public class SduSensorDataBroker extends HashMap<SduSensorListener, Integer> {
    /**
     * Notifies the subscribed listeners about new data capture for a sensor.
     *
     * @param data the data captured
     */
    public void notifyListeners(double data) {
        forEach((listener, address) -> listener.sduDataCaptured(address, data));
    }
}
