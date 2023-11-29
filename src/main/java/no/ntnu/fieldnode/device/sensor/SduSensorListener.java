package no.ntnu.fieldnode.device.sensor;

/**
 * A listener listening for SDU sensor data being captured.
 */
public interface SduSensorListener {
    /**
     * Triggers when SDU data has been captured by an SDU sensor.
     *
     * @param sensorAddress the address of the sensor that captured the data
     * @param data the sdu data captured
     */
    void sduDataCaptured(int sensorAddress, double data);
}
