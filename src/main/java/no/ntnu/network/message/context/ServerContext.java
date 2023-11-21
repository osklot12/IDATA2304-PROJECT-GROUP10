package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.ServerAgent;
import no.ntnu.network.centralserver.CentralHub;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;
import no.ntnu.network.centralserver.clientproxy.ControlPanelClientProxy;
import no.ntnu.network.centralserver.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.ServerLogger;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A context for processing server messages.
 */
public class ServerContext implements MessageContext {
    private final ServerAgent agent;
    private final CentralHub centralHub;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent the communication agent
     * @param centralHub the central hub to operate on
     */
    public ServerContext(ServerAgent agent, CentralHub centralHub) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because agent is null");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because central hub is null.");
        }

        this.agent = agent;
        this.centralHub = centralHub;
    }

    /**
     * Registers a field node client at the central server.
     *
     * @param fnst the field node system table for the field node
     * @param fnsm the field node status map for the field node
     * @param name the name of the field node
     * @return the assigned address for the field node client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerFieldNode(Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) throws ClientRegistrationException {
        return registerClient(new FieldNodeClientProxy(agent, fnst, fnsm, name));
    }

    /**
     * Registers a control panel client at the central server.
     *
     * @param compatibilityList the compatibility list for the control panel
     * @return the assigned address for the control panel client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerControlPanel(Set<DeviceClass> compatibilityList) throws ClientRegistrationException {
        return registerClient(new ControlPanelClientProxy(agent, compatibilityList));
    }

    /**
     * Registers a client proxy at the central hub.
     *
     * @param clientProxy the client proxy to register
     * @throws ClientRegistrationException thrown if registration fails
     */
    private int registerClient(ClientProxy clientProxy) throws ClientRegistrationException {
        int clientAddress = centralHub.registerClient(clientProxy);
        agent.registerClient();
        return clientAddress;
    }

    /**
     * Returns whether the client is registered or not.
     *
     * @return true if registered
     */
    public boolean isClientRegistered() {
        return agent.isClientRegistered();
    }

    @Override
    public void respond(ResponseMessage responseMessage) throws IOException {
        agent.sendResponse(responseMessage);
    }

    @Override
    public boolean acceptResponse(ResponseMessage response) {
        return agent.acceptResponse(response);
    }

    @Override
    public void logReceivingRequest(RequestMessage request) {
        ServerLogger.requestReceived(request, agent.getRemoteEntityAsString());
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        ServerLogger.responseReceived(response, agent.getRemoteEntityAsString());
    }
}
