package no.ntnu.fieldnode.device.sensor;

/**
 * An abstract class representing data captured by a sensor.
 */
public abstract class SensorData {
    private final int sensorAddress;

    /**
     * Constructor for the SensorData class.
     *
     * @param sensor the sensor that captured the data
     */
    public SensorData(int sensorAddress) {
        this.sensorAddress = sensorAddress;
    }

    /**
     * Returns the address for the sensor that captured the data.
     *
     * @return address of sensor
     */
    public int getSensorAddress() {
        return sensorAddress;
    }

    /**
     * Returns the data formatted to a String.
     *
     * @return data formatted as a String
     */
    public abstract String toFormattedData();
}
