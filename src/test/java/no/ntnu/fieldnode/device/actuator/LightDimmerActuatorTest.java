package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the LightDimmerActuator class.
 */
public class LightDimmerActuatorTest {
    Environment environment;
    Actuator actuator;
    double initialLuminosity;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        actuator = new LightDimmerActuator();
        initialLuminosity = environment.getSimulatedLuminosity();
        actuator.setEnvironment(environment);
    }

    /**
     * Tests that setting the LightDimmerActuator to state 0, will not change the luminosity in the environment.
     */
    @Test
    public void testChangeOfLuminosityState0() {
        actuator.setState(0);

        assertEquals(initialLuminosity, environment.getSimulatedLuminosity(), 0);
    }

    /**
     * Tests that setting the LightDimmerActuator to state 1, will decrease the luminosity in the environment by 1500.
     */
    @Test
    public void testChangeOfLuminosityState1() {
        actuator.setState(1);

        assertEquals(initialLuminosity - 1500, environment.getSimulatedLuminosity(), 0);
    }

    /**
     * Tests that setting the LightDimmerActuator to state 6, will increase the luminosity in the environment by 1500.
     */
    @Test
    public void testChangeOfLuminosityState6() {
        actuator.setState(6);

        assertEquals(initialLuminosity + 1500, environment.getSimulatedLuminosity(), 0);
    }
}