package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.TestControlCommAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit testing for the RegisterControlPanelRequest class.
 */
public class RegisterControlPanelRequestTest {
    TestControlCommAgent agent;
    CentralHub hub;
    ServerContext context;
    RegisterControlPanelRequest request;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        agent = new TestControlCommAgent();
        hub = new CentralHub();
        context = new ServerContext(agent, agent, hub);

        Set<DeviceClass> compatibilityList = new HashSet<>();
        compatibilityList.add(DeviceClass.S3);
        compatibilityList.add(DeviceClass.A1);
        request = new RegisterControlPanelRequest(compatibilityList, 8065);
    }

    /**
     * Tests that a valid processing of the message will respond with a confirmation.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testValidRegistration() throws IOException {
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof RegistrationConfirmationResponse<?>);
    }

    /**
     * Tests that an invalid processing of the message will respond with an error.
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