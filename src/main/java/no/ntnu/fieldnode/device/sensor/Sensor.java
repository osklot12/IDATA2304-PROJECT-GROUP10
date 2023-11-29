package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.device.Device;

/**
 * A device capturing data using some sensor for an environment.
 */
public interface Sensor extends Device {
    /**
     * Captures data in an environment.
     */
    void captureData() throws NoEnvironmentSetException;

    /**
     * Starts the sensors timer, capturing data independently.
     */
    void start();

    /**
     * Stops the sensors timer.
     */
    void stop();

    /**
     * Adds a listener to the sensor.
     *
     * @param sensorListener listener to add
     * @param fieldNodeAddress the address the listener uses for the field node
     */
    void addListener(SduSensorListener sensorListener, int fieldNodeAddress);

    /**
     * Removes a listener from the sensor.
     *
     * @param sensorListener the listener to remove
     */
    void removeListener(SduSensorListener sensorListener);
}
