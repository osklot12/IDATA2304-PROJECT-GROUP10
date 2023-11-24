package no.ntnu.network.message.request;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.TestAgent;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.response.HeartbeatResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code HeartBeatRequest} class.
 */
public class HeartbeatRequestTest {
    TestAgent agent;
    ControlPanelContext context;
    HeartbeatRequest<ControlPanelContext> request;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        agent = new TestAgent();
        ControlPanel controlPanel = new ControlPanel();
        context = new ControlPanelContext(agent, controlPanel);

        request = new HeartbeatRequest<>();
    }

    /**
     * Tests that a successfully processed heartbeat will respond with a confirmation as expected.
     */
    @Test
    public void testSuccessfulHeartbeat() throws IOException {
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof HeartbeatResponse);
    }
}