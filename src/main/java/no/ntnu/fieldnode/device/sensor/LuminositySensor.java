package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A sensor for measuring the luminosity in an environment.
 */
public class LuminositySensor extends SDUSensor {
    /**
     * Creates a new LuminositySensor.
     *
     * @param sensorNoise amount of noise interfering with the sensor. 0 is no noise, higher values adds more noise.
     *                                Must be a non-negative value.
     */
    public LuminositySensor(int sensorNoise) {
        super(DeviceClass.S3, "lux", sensorNoise);
    }

    @Override
    public void captureData() throws NoEnvironmentSetException {
        if (environment == null) {
            throw new NoEnvironmentSetException("Cannot capture data, because no environment is set for the sensor.");
        }

        this.sensorData = readAndProcessLuminosity();
        dataBroker.notifyListeners(getSensorData());
    }

    private double readAndProcessLuminosity() {
        double capturedHumidityValue = environment.getSimulatedLuminosity();
        return roundValue(addNoise(capturedHumidityValue));
    }
}
