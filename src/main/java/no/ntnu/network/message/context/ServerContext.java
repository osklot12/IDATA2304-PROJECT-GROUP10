package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SubscriptionException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.ServerLogger;

import java.util.Map;
import java.util.Set;

/**
 * A context for processing server messages.
 */
public class ServerContext extends MessageContext {
    private final CentralHub centralHub;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent the communication agent
     * @param centralHub the central hub to operate on
     */
    public ServerContext(CommunicationAgent agent, CentralHub centralHub) {
        super(agent);
        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because central hub is null.");
        }

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
        int clientAddress = centralHub.registerFieldNode(fnst, fnsm, name, agent);
        if (clientAddress != -1) {
            agent.setClientNodeAddress(clientAddress);
        }

        return clientAddress;
    }

    /**
     * Registers a control panel client at the central server.
     *
     * @param compatibilityList the compatibility list for the control panel
     * @return the assigned address for the control panel client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerControlPanel(Set<DeviceClass> compatibilityList) throws ClientRegistrationException {
        int clientAddress = centralHub.registerControlPanel(compatibilityList, agent);
        if (clientAddress != -1) {
            agent.setClientNodeAddress(clientAddress);
        }

        return clientAddress;
    }

    /**
     * Returns whether the client is registered or not.
     *
     * @return true if registered
     */
    public boolean isClientRegistered() {
        return agent.getClientNodeAddress() != -1;
    }

    /**
     * Returns the field node pool.
     *
     * @return the field node pool
     */
    public Map<Integer, String> getFieldNodePool() {
        return centralHub.getFieldNodePool();
    }

    /**
     * Subscribes a control panel to a field node.
     *
     * @param fieldNodeAddress the address of the field node to subscribe to
     * @return the field node client proxy subscribed to
     * @throws SubscriptionException thrown if subscribing fails
     */
    public FieldNodeClientProxy subscribeToFieldNode(int fieldNodeAddress) throws SubscriptionException {
        return centralHub.subscribeToFieldNode(agent, fieldNodeAddress);
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
