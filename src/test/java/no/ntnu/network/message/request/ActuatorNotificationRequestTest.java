package no.ntnu.network.message.request;

import no.ntnu.network.TestAgent;
import no.ntnu.network.centralserver.CentralHubTestFactory;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.ServerFnsmUpdatedResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.ServerFnsmUpdateRejectedError;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code ActuatorNotificationRequest} class.
 */
public class ActuatorNotificationRequestTest {
    TestAgent agent;
    CentralHub hub;
    ServerContext context;
    ActuatorNotificationRequest request;

    /**
     * Setting up for the following methods.
     */
    @Before
    public void setup() {
        agent = new TestAgent();
        hub = CentralHubTestFactory.getPopulatedHub();
        context = new ServerContext(agent, hub);
    }

    /**
     * Tests that a valid request will successfully update the servers' FNSM.
     */
    @Test
    public void testSuccessfulUpdate() throws IOException {
        // the client needs to be registered in order for the request to pass
        agent.setClientNodeAddress(0);

        request = new ActuatorNotificationRequest(2, 3);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof ServerFnsmUpdatedResponse);
    }

    /**
     * Tests that trying to process the message without having registered, will be responded with an error message.
     */
    @Test
    public void testProcessingWithoutRegistering() throws IOException {
        request = new ActuatorNotificationRequest(2, 3);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof AuthenticationFailedError<?>);
    }

    /**
     * Tests that trying to process the message with an invalid actuator address, will be responded with an error
     * message.
     */
    @Test
    public void testProcessingWithInvalidActuatorAddress() throws IOException {
        // the client needs to be registered in order for the request to pass
        agent.setClientNodeAddress(0);

        request = new ActuatorNotificationRequest(5, 3);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof ServerFnsmUpdateRejectedError);
    }
}