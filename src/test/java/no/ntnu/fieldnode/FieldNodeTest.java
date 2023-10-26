package no.ntnu.fieldnode;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.sensor.Sensor;
import no.ntnu.fieldnode.device.sensor.TemperatureSensor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit tests for the Field Node class.
 */
public class FieldNodeTest {
    Environment environment;

    FieldNode fieldNode;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        environment = new Environment();
        fieldNode = new FieldNode(environment);
    }

    /**
     * Tests that creation of a field node using invalid arguments, throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreationWithInvalidArguments() {
        fieldNode = new FieldNode(null);
    }

    /**
     * Tests that a valid sensor will be added successfully.
     */
    @Test
    public void testAddSensorValid() {
        Sensor sensor = new TemperatureSensor();
        assertEquals(0, fieldNode.addSensor(sensor));
    }

    /**
     * Tests that adding a duplicate sensor will throw an exception.
     */
    @Test
    public void testAddSensorInvalid() {
        Sensor sensor = new TemperatureSensor();
        fieldNode.addSensor(sensor);
        assertEquals(-1, fieldNode.addSensor(sensor));
    }

    /**
     * Tests that a valid actuator will be added successfully.
     */
    public void testAddActuatorValid() {
        
    }

    public void testAddActuatorInvalid() {

    }
}