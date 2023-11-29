package no.ntnu.network.message.sensordata;

/**
 * A receiver of sensor data.
 */
public interface SensorDataReceiver {
    /**
     * Receives SDU sensor data.
     *
     * @param fieldNodeAddress the address of the field node
     * @param sensorAddress the address of the sensor
     * @param data the sdu data
     */
    void receiveSduData(int fieldNodeAddress, int sensorAddress, double data);
}
