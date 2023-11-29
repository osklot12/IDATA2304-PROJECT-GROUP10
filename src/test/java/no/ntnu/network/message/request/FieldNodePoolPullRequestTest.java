package no.ntnu.network.message.request;

import no.ntnu.network.TestControlCommAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.centralserver.CentralHubTestFactory;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.FieldNodePoolResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code FieldNodePoolPullRequest} class.
 */
public class FieldNodePoolPullRequestTest {
    TestControlCommAgent agent;
    CentralHub hub;
    ServerContext context;
    FieldNodePoolPullRequest request;

    /**
     * Setting up for the following methods.
     */
    @Before
    public void setup() {
        agent = new TestControlCommAgent();
        hub = CentralHubTestFactory.getPopulatedHub();
        context = new ServerContext(agent, agent, hub);
        request = new FieldNodePoolPullRequest();
    }

    /**
     * Tests the case of a successfully processed request.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSuccessfulRequest() throws IOException {
        // a successful process requires the agent to be registered
        agent.setClientNodeAddress(0);

        request.process(context);

        assertTrue(agent.getResponseSent() instanceof FieldNodePoolResponse);
    }

    /**
     * Tests that processing the request for a non-registered client will respond with an authentication error.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testFailedRequest() throws IOException {
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof AuthenticationFailedError<?>);
    }
}