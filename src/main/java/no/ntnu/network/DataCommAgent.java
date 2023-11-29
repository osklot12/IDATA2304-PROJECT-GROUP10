package no.ntnu.network;

import no.ntnu.network.message.sensordata.SensorDataMessage;

import java.io.IOException;

/**
 * A communication agent responsible for sending sensor data messages to another entity in the network.
 */
public interface DataCommAgent {
    /**
     * Sends a sensor data message to a remote network entity.
     *
     * @param sensorData the sensor data message to send
     * @throws IOException thrown if an I/O exception occurs
     */
    void sendSensorData(SensorDataMessage sensorData) throws IOException;
}
