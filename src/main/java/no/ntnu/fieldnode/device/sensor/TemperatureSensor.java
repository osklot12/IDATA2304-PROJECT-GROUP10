package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A sensor for measuring temperatures in an environment.
 */
public class TemperatureSensor extends SDUSensor {
    /**
     * Creates a new TemperatureSensor.
     *
     * @param sensorNoise amount of noise interfering with the sensor. 0 is no noise, higher values adds more noise.
     *                    Must be a non-negative value.
     */
    public TemperatureSensor(int sensorNoise) {
        super(DeviceClass.S1, "C", sensorNoise);
    }

    @Override
    public void captureData() throws NoEnvironmentSetException {
        if (environment == null) {
            throw new NoEnvironmentSetException("Cannot capture data, because no environment is set for the sensor.");
        }

        this.sensorData = readAndProcessTemperature();
        dataBroker.notifyListeners(getSensorData());
    }

    private double readAndProcessTemperature() {
        double capturedTempValue = environment.getSimulatedTemperature();
        return roundValue(addNoise(capturedTempValue));
    }
}