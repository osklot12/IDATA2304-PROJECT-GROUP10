package no.ntnu.fieldnode.device.sensor;

import no.ntnu.environment.Environment;
import no.ntnu.exception.NoEnvironmentSetException;
import no.ntnu.fieldnode.device.actuator.FanActuator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the TemperatureSensor class.
 * This class also tests the logic for the abstract SDUSensor class.
 */
public class TemperatureSensorTest {
    Environment environment;
    TemperatureSensor temperatureSensor;
    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        temperatureSensor = new TemperatureSensor(0);
        temperatureSensor.setEnvironment(environment);
    }

    /**
     * Tests that the temperature sensor captures the actual temperature of the environment.
     */
    @Test
    public void testCaptureOfEnvironmentTemperature() {
        double actualTemperature = environment.getSimulatedTemperature();

        temperatureSensor.captureData();

        assertEquals(actualTemperature, temperatureSensor.getSensorData().value(), 0);
    }

    /**
     * Tests that trying to capture data with no environment set, will throw an NoEnvironmentSetException.
     */
    @Test(expected = NoEnvironmentSetException.class)
    public void testCaptureOfNullEnvironment() {
        temperatureSensor.setEnvironment(null);

        temperatureSensor.captureData();
    }

    /**
     * Tests that the sensor will capture the proper simulated temperature, even if the environment is modified.
     */
    @Test
    public void testCaptureOfAModifiedEnvironment() {
        FanActuator fan = new FanActuator();
        fan.setEnvironment(environment);

        fan.setState(2);
        temperatureSensor.captureData();

        assertEquals(environment.getSimulatedTemperature(), temperatureSensor.getSensorData().value(), 0);
    }
}