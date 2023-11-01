package no.ntnu.network.centralserver;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;
import no.ntnu.network.centralserver.clientproxy.ControlPanelClientProxy;
import no.ntnu.network.centralserver.clientproxy.FieldNodeClientProxy;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit testing for the ClientRegister class.
 */
public class ClientRegisterTest {
    FieldNodeClientProxy fieldNode;
    int fieldNodeAddress;
    ControlPanelClientProxy controlPanel;
    int controlPanelAddress;
    ClientRegister register;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        Socket fieldNodeSocket = new Socket();
        Map<Integer, DeviceClass> fnst = new HashMap<>();
        Map<Integer, Integer> fnsm = new HashMap<>();
        String name = "greenhouseFieldNode";
        fieldNode = new FieldNodeClientProxy(fieldNodeSocket, fnst, fnsm, name);

        Set<DeviceClass> compatibilityList = new HashSet<>();
        Socket controlPanelSocket = new Socket();
        controlPanel = new ControlPanelClientProxy(controlPanelSocket, compatibilityList);

        register = new ClientRegister();
        fieldNodeAddress = register.addClient(fieldNode);
        controlPanelAddress = register.addClient(controlPanel);
    }

    /**
     * Tests that adding a client that already exists in the register, will throw an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateClient() {
        register.addClient(fieldNode);
    }

    /**
     * Tests that the getClientProxy() method returns the expected client proxy.
     */
    @Test
    public void getClientProxy() {
        ClientProxy retrieved = register.getClientProxy(fieldNodeAddress);

        assertEquals(fieldNode, retrieved);
    }

    /**
     * Tests that the getFieldNodePool() method return the field node pool as expected.
     */
    @Test
    public void testGetFieldNodePool() {
        Map<Integer, FieldNodeClientProxy> fieldNodePool = register.getFieldNodePool();

        assertEquals(1, fieldNodePool.entrySet().size());
        assertTrue(fieldNodePool.containsKey(fieldNodeAddress));
        assertTrue(fieldNodePool.containsValue(fieldNode));
    }
}