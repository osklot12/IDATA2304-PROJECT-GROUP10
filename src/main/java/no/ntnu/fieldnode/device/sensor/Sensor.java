package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.device.Device;
import no.ntnu.environment.Environment;

/**
 * A device capturing data using some sensor for an environment.
 */
public interface Sensor extends Device {
    /**
     * Captures data in an environment.
     */
    void captureData() throws NoEnvironmentSetException;

    /**
     * Pushes the latest data captured by the sensor to a sensor listener.
     *
     * @param listener a sensor listener
     */
    void pushData(SensorListener listener);

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
     * @return true if successfully added
     */
    boolean addListener(SensorListener sensorListener);

    /**
     * Removes a listener from the sensor.
     *
     * @param sensorListener the listener to remove
     * @return true if successfully removed
     */
    boolean removeListener(SensorListener sensorListener);
}
