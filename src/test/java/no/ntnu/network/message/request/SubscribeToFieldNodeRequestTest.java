package no.ntnu.network.message.request;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.TestControlCommAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.centralserver.CentralHubTestFactory;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.SubscribedToFieldNodeResponse;
import no.ntnu.network.message.response.error.AuthenticationFailedError;
import no.ntnu.network.message.response.error.SubscriptionError;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code SubscribeToFieldNodeRequest} class.
 */
public class SubscribeToFieldNodeRequestTest {
    TestControlCommAgent agent;
    CentralHub hub;
    ServerContext context;
    SubscribeToFieldNodeRequest request;
    Set<DeviceClass> compatibilityList;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        agent = new TestControlCommAgent();
        hub = CentralHubTestFactory.getPopulatedHub();
        context = new ServerContext(agent, agent, hub);
        compatibilityList = new HashSet<>();
        compatibilityList.add(DeviceClass.A1);
    }

    /**
     * Tests the case of a successfully processed request.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSuccessfulRequest() throws IOException {
        // the requesting agent needs to be registered as a control panel
        int clientAddress = hub.registerControlPanel(compatibilityList, agent, agent.getDataCommAgent(1023));
        agent.setClientNodeAddress(clientAddress);

        request = new SubscribeToFieldNodeRequest(0);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof SubscribedToFieldNodeResponse);
    }

    /**
     * Tests that trying to subscribe when not registered as a control panel, will be responded with an error message.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSubscribingWhenNotRegistered() throws IOException {
        request = new SubscribeToFieldNodeRequest(0);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof AuthenticationFailedError<?>);
    }

    /**
     * Tests that trying to subscribe to a non-existing field node will be responded with an error message.
     */
    @Test
    public void testSubscribingToNonExistingFieldNode() throws IOException {
        // the requesting agent needs to be registered as a control panel
        int clientAddress = hub.registerControlPanel(compatibilityList, agent, agent.getDataCommAgent(1203));
        agent.setClientNodeAddress(clientAddress);

        request = new SubscribeToFieldNodeRequest(20);
        request.process(context);

        assertTrue(agent.getResponseSent() instanceof SubscriptionError<?>);
    }
}