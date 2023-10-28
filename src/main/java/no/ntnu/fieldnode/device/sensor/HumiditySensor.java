package no.ntnu.fieldnode.device.sensor;

import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A sensor for measuring the humidity in an environment.
 */
public class HumiditySensor extends SDUSensor {
    /**
     * Creates a new HumiditySensor.
     *
     * @param sensorNoise amount of noise interfering with the sensor. 0 is no noise, higher values adds more noise.
     *                           Must be a non-negative value.
     */
    public HumiditySensor(int sensorNoise) {
        super(DeviceClass.S2, "%", sensorNoise);
    }

    @Override
    public void captureData() throws NoEnvironmentSetException {
        if (environment == null) {
            throw new NoEnvironmentSetException("Cannot capture data, because no environment is set for the sensor.");
        }

        double capturedHumidity = readAndProcessHumidity();
        this.sensorData = new SDUSensorData(capturedHumidity, unit);
        dataBroker.notifyListeners(this);
    }

    private double readAndProcessHumidity() {
        double capturedHumidityValue = environment.getSimulatedHumidity();
        return roundValue(addNoise(capturedHumidityValue));
    }
}
