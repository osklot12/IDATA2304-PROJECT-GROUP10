package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.TestAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.context.ServerContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * JUnit testing for the RegisterFieldNodeRequest class.
 */
public class RegisterFieldNodeRequestTest {
    TestAgent agent;
    CentralHub hub;
    ServerContext context;
    Map<Integer, DeviceClass> fnst;
    Map<Integer, Integer> fnsm;
    String name;
    RegisterFieldNodeRequest request;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        agent = new TestAgent();
        hub = new CentralHub();
        context = new ServerContext(agent, hub);

        fnst = new HashMap<>();
        fnst.put(1, DeviceClass.A2);
        fnst.put(2, DeviceClass.S3);

        fnsm = new HashMap<>();
        fnsm.put(1, 8);
        fnsm.put(3, 16);
        fnsm.put(8, 19);

        name = "testFieldNode";

        request = new RegisterFieldNodeRequest(fnst, fnsm, name);
    }

    /**
     * Tests the case where the registration of a field node should be successful.
     */
    @Test
    public void testSuccessfulRegistration() throws IOException {
        request.process(context);

        assertTrue(agent.getClientNodeAddress() != -1);
    }
}