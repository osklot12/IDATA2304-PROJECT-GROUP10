package no.ntnu.fieldnode;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.fieldnode.device.actuator.Actuator;
import no.ntnu.fieldnode.device.actuator.FanActuator;
import no.ntnu.fieldnode.device.actuator.HumidifierActuator;
import no.ntnu.fieldnode.device.actuator.LightDimmerActuator;
import no.ntnu.fieldnode.device.sensor.HumiditySensor;
import no.ntnu.fieldnode.device.sensor.LuminositySensor;
import no.ntnu.fieldnode.device.sensor.Sensor;
import no.ntnu.fieldnode.device.sensor.TemperatureSensor;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
     * Tests that a valid sensor will be added successfully.
     */
    @Test
    public void testAddSensorValid() {
        Sensor sensor = new TemperatureSensor(0);
        assertEquals(0, fieldNode.addDevice(sensor));
    }

    /**
     * Tests that adding a duplicate sensor will throw an exception.
     */
    @Test
    public void testAddSensorInvalid() {
        Sensor sensor = new TemperatureSensor(0);
        fieldNode.addDevice(sensor);
        assertEquals(-1, fieldNode.addDevice(sensor));
    }

    /**
     * Tests that a valid actuator will be added successfully.
     */
    @Test
    public void testAddActuatorValid() {
        Actuator actuator = new FanActuator();
        assertEquals(0, fieldNode.addDevice(actuator));
    }

    /**
     * Tests that adding a duplicate actuator will throw an exception.
     */
    @Test
    public void testAddActuatorInvalid() {
        Actuator actuator = new FanActuator();
        fieldNode.addDevice(actuator);
        assertEquals(-1, fieldNode.addDevice(actuator));
    }

    /**
     * Tests that setting the state for an actuator actually changes the environment.
     */
    @Test
    public void testSetActuatorState() {
        Actuator fan = new FanActuator();
        fieldNode.addDevice(fan);
        double initialTemperature = environment.getSimulatedTemperature();

        fan.setState(3);

        assertTrue(environment.getSimulatedTemperature() < initialTemperature);
    }

    /**
     * Tests that the field node returns its proper FNST.
     */
    @Test
    public void testGetFNST() {
        Sensor temperatureSensor = new TemperatureSensor(0);
        Sensor humiditySensor = new HumiditySensor(0);
        Sensor luminositySensor = new LuminositySensor(0);

        Actuator fanActuator = new FanActuator();
        Actuator humidifierActuator = new HumidifierActuator();
        Actuator lightDimmerActuator = new LightDimmerActuator();

        int tSensorAddress = fieldNode.addDevice(temperatureSensor);
        int hSensorAddress = fieldNode.addDevice(humiditySensor);
        int lSensorAddress = fieldNode.addDevice(luminositySensor);

        int fanAddress = fieldNode.addDevice(fanActuator);
        int humidifierAddress = fieldNode.addDevice(humidifierActuator);
        int lightDimmerAddress = fieldNode.addDevice(lightDimmerActuator);

        Map<Integer, DeviceClass> fnst = fieldNode.getFNST();

        assertEquals(fnst.size(), 6);
        assertEquals(temperatureSensor.getDeviceClass(), fnst.get(tSensorAddress));
        assertEquals(humiditySensor.getDeviceClass(), fnst.get(hSensorAddress));
        assertEquals(luminositySensor.getDeviceClass(), fnst.get(lSensorAddress));
        assertEquals(fanActuator.getDeviceClass(), fnst.get(fanAddress));
        assertEquals(humidifierActuator.getDeviceClass(), fnst.get(humidifierAddress));
        assertEquals(lightDimmerActuator.getDeviceClass(), fnst.get(lightDimmerAddress));
    }
}