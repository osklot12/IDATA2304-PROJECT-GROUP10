package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.EnvironmentNotSupportedException;
import no.ntnu.fieldnode.FieldNode;
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

    public void setEnvironment(Environment environment) throws EnvironmentNotSupportedException {

    }

    @Override
    public void captureData(Environment environment, int sensorAddress) {
        double capturedTemperature = environment.getSimulatedTemperature();
        sensorData = new SDUSensorData(sensorAddress, capturedTemperature, "C");
    }

    @Override
    public SensorData getSensorData() {
        return this.sensorData;
    }

    @Override
    public DeviceClass getDeviceClass() {
        return deviceClass;
    }

    @Override
    public boolean connectToFieldNode(FieldNode fieldNode) {
        return false;
    }

    @Override
    public boolean disconnectFromFieldNode(FieldNode fieldNode) {
        return false;
    }
}