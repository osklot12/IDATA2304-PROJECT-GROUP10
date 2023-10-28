package no.ntnu.fieldnode.device.sensor;

/**
 * A listener listening for sensor data being captured.
 */
public interface SensorListener {
    /**
     * Triggers when a sensor captures some data, notifying any listener that new data is available.
     *
     * @param sensor the sensor that captured data
     */
    void sensorDataCaptured(Sensor sensor);

    /**
     * Triggers when the listeners want to receive data from a sensor.
     *
     * @param sensor the sensor to receive data from
     */
    void receiveSensorData(Sensor sensor);
}
