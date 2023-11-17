package no.ntnu.broker;

import no.ntnu.controlpanel.virtual.VirtualSDUSensor;
import no.ntnu.controlpanel.virtual.VirtualSDUSensorListener;

/**
 * A class representing a broker for new virtual sdu data.
 */
public class VirtualSDUDataBroker extends SubscriberList<VirtualSDUSensorListener> {
    /**
     * Notifies the subscribed listeners that new sdu data has arrived for a virtual sdu sensor.
     *
     * @param virtualSensor the sensor that stored the new sdu data
     */
    public void notifyListeners(VirtualSDUSensor virtualSensor) {
        getSubscribers().forEach(
                (listener) -> {
                    listener.newSDUData(virtualSensor);
                }
        );
    }
}
