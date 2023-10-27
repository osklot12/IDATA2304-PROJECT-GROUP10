package no.ntnu.fieldnode.device.sensor;

/**
 * Sensor data capturing a single Double-value of data with a given SI-unit.
 */
public class SDUSensorData extends SensorData {
    private final double value;

    private final String unit;

    /**
     * Creates a new SDUSensorData object.
     *
     * @param sensorAddress the address of the sensor that captured the data
     * @param value the value of the data
     * @param unit the unit of the data
     */
    public SDUSensorData(int sensorAddress, double value, String unit) {
        super(sensorAddress);
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String toFormattedData() {
        return value + " " + unit;
    }
}
