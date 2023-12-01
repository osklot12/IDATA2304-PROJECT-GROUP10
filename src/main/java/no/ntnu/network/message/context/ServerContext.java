package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.NoSuchAddressException;
import no.ntnu.exception.SubscriptionException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.representation.FieldNodeInformation;
import no.ntnu.network.sensordataprocess.UdpDataSink;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.request.FieldNodeActivateActuatorRequest;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.logger.ServerLogger;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A message context for processing server messages.
 */
public class ServerContext extends MessageContext {
    private final UdpDataSink dataAgentProvider;
    private final CentralHub centralHub;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent the communication agent
     * @param dataAgentProvider
     * @param centralHub the central hub to operate on
     */
    public ServerContext(ControlCommAgent agent, UdpDataSink dataAgentProvider, CentralHub centralHub) {
        super(agent);
        if (dataAgentProvider == null) {
            throw new IllegalStateException("Cannot create ServerContext, because dataAgentProvider is null.");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because central hub is null.");
        }

        this.dataAgentProvider = dataAgentProvider;
        this.centralHub = centralHub;
    }

    /**
     * Registers a field node client at the central server.
     *
     * @param fieldNodeInformation information about the field node
     * @return the assigned address for the field node client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerFieldNodeClient(FieldNodeInformation fieldNodeInformation) throws ClientRegistrationException {
        int clientAddress = centralHub.registerFieldNode(fieldNodeInformation, agent);
        if (clientAddress != -1) {
            agent.setClientNodeAddress(clientAddress);
        }

        return clientAddress;
    }

    /**
     * Registers a control panel client at the central server.
     *
     * @param compatibilityList the compatibility list for the control panel
     * @param dataSinkPortNumber the port number for the control panel data sink
     * @return the assigned address for the control panel client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerControlPanelClient(Set<DeviceClass> compatibilityList, int dataSinkPortNumber) throws ClientRegistrationException {
        int clientAddress = -1;

        try {
            DataCommAgent dataAgent = dataAgentProvider.getDataCommAgent(dataSinkPortNumber);
            clientAddress = centralHub.registerControlPanel(compatibilityList, agent, dataAgent);
            if (clientAddress != -1) {
                agent.setClientNodeAddress(clientAddress);
            }
        } catch (IOException e) {
            throw new ClientRegistrationException(e.getMessage());
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

    /**
     * Unsubscribes a control panel from a field node.
     *
     * @param fieldNodeAddress the address of the field node to unsubscribe from
     * @throws SubscriptionException thrown if unsubscribing fails
     */
    public void unsubscribeFromFieldNode(int fieldNodeAddress) throws SubscriptionException {
        centralHub.unsubscribeFromFieldNode(agent, fieldNodeAddress);
    }

    /**
     * Updates the address for a given actuator for a given field node.
     * The information is updated locally at the central server, and does not provoke a request to the actual field
     * node.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState the new state to set
     * @throws NoSuchAddressException thrown if one of the addresses is invalid
     */
    public void updateLocalActuatorState(int actuatorAddress, int newState) throws NoSuchAddressException {
        centralHub.setLocalActuatorState(agent.getClientNodeAddress(), actuatorAddress, newState);
    }

    /**
     * Requests a remote field node to change state for a given actuator.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state to set
     */
    public void requestActuatorActivationForFieldNode(int fieldNodeAddress, int actuatorAddress, int newState) throws IOException {
        FieldNodeClientProxy proxy = centralHub.getFieldNodeProxy(fieldNodeAddress);
        // checks if a field node with the given address exists
        if (proxy != null) {
            // checks if the field node has an actuator with the given address
            if (proxy.getFNSM().containsKey(actuatorAddress)) {
                ControlCommAgent agent = proxy.getAgent();
                agent.sendRequest(new FieldNodeActivateActuatorRequest(actuatorAddress, newState));
            } else {
                throw new IOException("Cannot request actuator activation for actuator " +
                        "with address " + actuatorAddress + " on field node " + fieldNodeAddress +
                        ", because no such actuator exists.");
            }
        } else {
            throw new IOException("Cannot request actuator activation for field node with" +
                    " address " + fieldNodeAddress + ", because no such field node exists.");
        }
    }

    /**
     * Returns the field node client proxy for a given field node address.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the field node client proxy, null if not found
     */
    public FieldNodeClientProxy getFieldNodeProxy(int fieldNodeAddress) {
        return centralHub.getFieldNodeProxy(fieldNodeAddress);
    }

    /**
     * Returns the port number used for the sensor data process.
     *
     * @return sensor data service port number
     */
    public int getSensorDataServicePortNumber() {
        return CentralServer.DATA_PORT_NUMBER;
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
