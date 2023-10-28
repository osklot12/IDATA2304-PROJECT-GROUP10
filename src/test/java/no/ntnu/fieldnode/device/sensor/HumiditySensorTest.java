package no.ntnu.fieldnode.device.sensor;

import no.ntnu.environment.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the HumiditySensor class.
 */
public class HumiditySensorTest {
    Environment environment;

    HumiditySensor sensor;
    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        sensor = new HumiditySensor(0);
        sensor.setEnvironment(environment);
    }

    /**
     * Tests that the humidity sensor captures the actual temperature of the environment.
     */
    @Test
    public void testCaptureOfEnvironmentHumidity() {
        double actualTemperature = environment.getSimulatedHumidity();

        sensor.captureData();

        assertEquals(actualTemperature, sensor.getSensorData().value(), 0);
    }
}