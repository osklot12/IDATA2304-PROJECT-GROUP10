package no.ntnu.network.connectionservice.sensordatarouter;

import no.ntnu.network.message.sensordata.SensorDataMessage;

/**
 * A destination for received sensor data messages.
 */
public interface SensorDataDestination {
    /**
     * Receives routed sensor data at the destination.
     *
     * @param sensorData the sensor data message to transmit
     */
    void receiveSensorData(SensorDataMessage sensorData);
}
