package no.ntnu.fieldnode;

/**
 * A listener listening for events for a field node.
 */
public interface FieldNodeListener {
    /**
     * Triggers when an actuator changes state.
     *
     * @param actuatorAddress the address of the actuator
     * @param netState the new state of the actuator
     */
    void actuatorStateChange(int actuatorAddress, int netState);

    /**
     * Triggers when new sensor data is captured.
     *
     * @param sensorAddress the address of the sensor
     * @param data the data itself
     */
    void sensorDataCapture(int sensorAddress, double data);
}
