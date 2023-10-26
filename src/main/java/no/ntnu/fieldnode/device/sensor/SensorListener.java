package no.ntnu.fieldnode.device.sensor;

/**
 * A listener listening for sensor data being captured.
 */
public interface SensorListener {
    /**
     * Triggers when a sensor captures some data.
     *
     * @param sensor the sensor that captured data
     */
    void dataCaptured(Sensor sensor);
}
