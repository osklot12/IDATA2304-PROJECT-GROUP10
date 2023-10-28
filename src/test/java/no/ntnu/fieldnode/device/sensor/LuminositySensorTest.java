package no.ntnu.fieldnode.device.sensor;

import no.ntnu.environment.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the LuminositySensor class.
 */
public class LuminositySensorTest {
    Environment environment;

    LuminositySensor sensor;
    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        sensor = new LuminositySensor(0);
        sensor.setEnvironment(environment);
    }

    /**
     * Tests that the luminosity sensor captures the actual luminosity in the environment.
     */
    @Test
    public void testCaptureOfEnvironmentLuminosity() {
        double actualLuminosity = environment.getSimulatedLuminosity();

        sensor.captureData();

        assertEquals(actualLuminosity, sensor.getSensorData().value(), 0);
    }
}