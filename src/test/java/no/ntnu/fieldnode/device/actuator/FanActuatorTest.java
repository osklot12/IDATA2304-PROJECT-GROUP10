package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit tests for the FanActuator class.
 */
public class FanActuatorTest {
    Environment environment;
    FanActuator fanActuator;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        fanActuator = new FanActuator();
        fanActuator.setEnvironment(environment);
    }

    /**
     * Tests that a fan set to state 0 will not affect the environment at all, since the fan is turned off.
     */
    @Test
    public void testFanEffectForState0() {
        double beforeValue = environment.getSimulatedTemperature();

        fanActuator.setState(0);

        assertEquals(beforeValue, environment.getSimulatedTemperature(), 0);
    }

    /**
     * Tests that a fan set to state 1 will decrease the environment temperature by 1 degree.
     */
    @Test
    public void testFanEffectForState1() {
        double beforeValue = environment.getSimulatedTemperature();

        fanActuator.setState(1);

        assertEquals(beforeValue - 1, environment.getSimulatedTemperature(), 0);
    }

    /**
     * Tests that a fan set to state 2 will decrease the environment temperature by 2 degrees.
     */
    @Test
    public void testFanEffectForState2() {
        double beforeValue = environment.getSimulatedTemperature();

        fanActuator.setState(2);

        assertEquals(beforeValue - 2, environment.getSimulatedTemperature(), 0);
    }

    /**
     * Tests that a fan set to state 3 will decrease the environment temperature by 3 degrees.
     */
    @Test
    public void testFanEffectForState3() {
        double beforeValue = environment.getSimulatedTemperature();

        fanActuator.setState(3);

        assertEquals(beforeValue - 3, environment.getSimulatedTemperature(), 0);
    }

    /**
     * Tests that an environment can be affected than more than one fan.
     */
    @Test
    public void testFanEffectWithTwoFans() {
        double beforeValue = environment.getSimulatedTemperature();
        FanActuator secondFanActuator = new FanActuator();
        secondFanActuator.setEnvironment(environment);

        fanActuator.setState(3);
        secondFanActuator.setState(3);

        assertEquals(beforeValue - 6, environment.getSimulatedTemperature(), 0);
    }

    /**
     * Tests that removing the fan from the environment (setting it to another environment), will stop the affect
     * it has on the environment.
     */
    @Test
    public void testRemovalOfFan() {
        double beforeValue = environment.getSimulatedTemperature();

        fanActuator.setState(3);
        fanActuator.setEnvironment(null);

        assertEquals(beforeValue, environment.getSimulatedTemperature(), 0);
    }
}