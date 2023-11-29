package no.ntnu.fieldnode.device.sensor;

import java.util.Random;

/**
 * A class responsible for generating noise to a data reading for a sensor, simulating somewhat a realistic
 * data capture.
 */
public class SduSensorNoiseGenerator {
    /**
     * Adds noise to a captured sensor data double value.
     *
     * @param value the value to add noise to
     * @param intensity the intensity of the noise
     * @return the value with added noise
     */
    public static double generateNoise(double value, int intensity) {
        double randomNoise = (((new Random().nextDouble()) - 0.5) / 4) * intensity;
        return value + randomNoise;
    }
}
