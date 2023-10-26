package no.ntnu.fieldnode.device.sensor;

import no.ntnu.fieldnode.device.Device;
import no.ntnu.environment.Environment;

/**
 * A device capturing data using some sensor.
 */
public interface Sensor extends Device {
    /**
     * Captures data in an environment.
     *
     * @param environment the environment to captured data from
     * @param sensorAddress the sensor address to capture data for
     */
    void captureData(Environment environment, int sensorAddress);

    /**
     * Returns the current data for the sensor.
     *
     * @return current sensor data
     */
    SensorData getSensorData();
}
