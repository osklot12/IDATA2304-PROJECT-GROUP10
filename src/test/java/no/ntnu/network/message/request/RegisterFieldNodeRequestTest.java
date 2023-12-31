package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.TestControlCommAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.representation.FieldNodeInformation;
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
    TestControlCommAgent agent;
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
        agent = new TestControlCommAgent();
        hub = new CentralHub();
        context = new ServerContext(agent, agent, hub);

        fnst = new HashMap<>();
        fnst.put(1, DeviceClass.A2);
        fnst.put(2, DeviceClass.S3);

        fnsm = new HashMap<>();
        fnsm.put(1, 8);
        fnsm.put(3, 16);
        fnsm.put(8, 19);

        name = "testFieldNode";

        request = new RegisterFieldNodeRequest(new FieldNodeInformation(fnst, fnsm, name));
    }

    /**
     * Tests the case where the registration of a field node should be successful.
     */
    @Test
    public void testSuccessfulRegistration() throws IOException {
        request.process(context);

        assertNotEquals(agent.getClientNodeAddress(), -1);
        assertNotNull(hub.getFieldNodeClientProxy(agent.getClientNodeAddress()));
        assertTrue(agent.getResponseSent() instanceof RegistrationConfirmationResponse<?>);
    }

    /**
     * Tests that an invalid processing of the request will respond with an error.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testInvalidRegistration() throws IOException {
        request.process(context);
        // trying to register the same client twice is not valid
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof RegistrationDeclinedError);
    }
}