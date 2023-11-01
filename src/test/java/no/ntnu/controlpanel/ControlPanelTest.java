package no.ntnu.controlpanel;

import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.controlpanel.virtual.VirtualSDUSensor;
import no.ntnu.controlpanel.virtual.VirtualStandardActuator;
import no.ntnu.fieldnode.device.DeviceClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the ControlPanel class.
 */
public class ControlPanelTest {
    int virtualSensor1Address;
    int virtualSensor2Address;
    int virtualActuator1Address;
    int virtualActuator2Address;
    VirtualFieldNode fieldNode1;
    int virtualFieldNode1Address;
    int virtualFieldNode2Address;
    ControlPanel controlPanel;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        VirtualSDUSensor sensor1 = new VirtualSDUSensor(DeviceClass.S1, 2);
        VirtualSDUSensor sensor2 = new VirtualSDUSensor(DeviceClass.S2, 20);

        VirtualStandardActuator actuator1 = new VirtualStandardActuator(DeviceClass.A1, 0);
        VirtualStandardActuator actuator2 = new VirtualStandardActuator(DeviceClass.A2, 0);

        fieldNode1 = new VirtualFieldNode();
        VirtualFieldNode fieldNode2 = new VirtualFieldNode();

        virtualSensor1Address = 0;
        fieldNode1.addVirtualDevice(virtualActuator1Address, sensor1);

        virtualSensor2Address = 0;
        fieldNode2.addVirtualDevice(virtualSensor2Address, sensor2);

        virtualActuator1Address = 1;
        fieldNode1.addVirtualDevice(virtualActuator1Address, actuator1);

        virtualActuator2Address = 1;
        fieldNode2.addVirtualDevice(virtualActuator2Address, actuator2);

        controlPanel = new ControlPanel();
        virtualFieldNode1Address = controlPanel.addVirtualFieldNode(fieldNode1);
        virtualFieldNode2Address = controlPanel.addVirtualFieldNode(fieldNode2);
    }

    /**
     * Tests that adding a virtual field node to the control panel that already exists, will return -1, indicating
     * that an error occurred.
     */
    @Test
    public void testAddingDuplicateFieldNode() {
        int address = controlPanel.addVirtualFieldNode(fieldNode1);

        assertEquals(-1, address);
    }

    /**
     * Tests that accessing a virtual field node by its address with retrieve the correct virtual field node.
     */
    @Test
    public void getVirtualFieldNode() {
        VirtualFieldNode retrieved = controlPanel.getVirtualFieldNode(virtualFieldNode1Address);

        assertEquals(fieldNode1, retrieved);
    }
}