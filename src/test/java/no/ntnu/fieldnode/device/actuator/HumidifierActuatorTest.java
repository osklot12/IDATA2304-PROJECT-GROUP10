package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the HumidifierActuator class.
 */
public class HumidifierActuatorTest {
    Environment environment;
    Actuator actuator;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        actuator = new HumidifierActuator();
        actuator.setEnvironment(environment);
    }

    /**
     * Tests that the HumidifierActuator does not change the humidity in state 0.
     */
    @Test
    public void testChangeOfHumidityState0() {
        double initialHumidity = environment.getSimulatedHumidity();

        actuator.setState(0);

        assertEquals(initialHumidity, environment.getSimulatedHumidity(), 0);
    }

    /**
     * Tests that the HumidifierActuator does lower the humidity in state 2.
     */
    @Test
    public void testChangeOfHumidityState2() {
        double initialHumidity = environment.getSimulatedHumidity();

        actuator.setState(2);

        assertEquals(initialHumidity - 20, environment.getSimulatedHumidity(), 0);
    }

    /**
     * Tests that the HumidifierActuator does increase the humidity in state 3.
     */
    @Test
    public void testChangeOfHumidityState3() {
        double initialHumidity = environment.getSimulatedHumidity();

        actuator.setState(3);

        assertEquals(initialHumidity + 20, environment.getSimulatedHumidity(), 0);
    }
}