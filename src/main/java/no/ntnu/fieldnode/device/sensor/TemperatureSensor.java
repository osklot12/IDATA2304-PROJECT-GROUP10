package no.ntnu.fieldnode.device.sensor;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.environment.Environment;

/**
 * A sensor for measuring temperatures.
 */
public class TemperatureSensor implements Sensor {
    private final DeviceClass deviceClass;

    private SensorData sensorData;

    /**
     * Creates a new TemperatureSensor.
     */
    public TemperatureSensor() {
        this.deviceClass = DeviceClass.S1;
        this.sensorData = null;
    }

    public void captureData(Environment environment, int sensorAddress) {
        int capturedTemperature = environment.getSimulatedTemperature();
        sensorData = new SDUSensorData(sensorAddress, capturedTemperature, "C");
    }

    public SensorData getSensorData() {
        return this.sensorData;
    }

    public DeviceClass getDeviceClass() {
        return deviceClass;
    }
}