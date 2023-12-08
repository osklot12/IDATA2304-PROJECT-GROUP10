package no.ntnu.network.message.context;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.NoSuchAddressException;
import no.ntnu.exception.SubscriptionException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.tools.logger.SimpleLogger;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.representation.FieldNodeInformation;
import no.ntnu.network.sensordataprocess.UdpDataCommAgentProvider;
import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.request.FieldNodeActivateActuatorRequest;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.eventformatter.ServerEventFormatter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A message context for processing server messages.
 */
public class ServerContext extends MessageContext {
    private final UdpDataCommAgentProvider udpDataSink;
    private final CentralHub centralHub;
    private final Set<SimpleLogger> loggers;

    /**
     * Creates a new CentralServerContext.
     *
     * @param agent       the communication agent
     * @param udpDataSink the
     * @param centralHub  the central hub to operate on
     */
    public ServerContext(ControlCommAgent agent, UdpDataCommAgentProvider udpDataSink, CentralHub centralHub, Set<SimpleLogger> loggers) {
        super(agent);
        if (udpDataSink == null) {
            throw new IllegalStateException("Cannot create ServerContext, because dataAgentProvider is null.");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerContext, because central hub is null.");
        }

        if (loggers == null) {
            throw new IllegalArgumentException("Cannot create Servercontext, because loggers is null.");
        }

        this.udpDataSink = udpDataSink;
        this.centralHub = centralHub;
        this.loggers = loggers;
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
     * @param compatibilityList  the compatibility list for the control panel
     * @param dataSinkPortNumber the port number for the control panel data sink
     * @return the assigned address for the control panel client
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerControlPanelClient(Set<DeviceClass> compatibilityList, int dataSinkPortNumber) throws ClientRegistrationException {
        int clientAddress = -1;

        try {
            DataCommAgent dataAgent = udpDataSink.getDataCommAgent(dataSinkPortNumber);
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
     * Deregisters the connected client.
     */
    public void deregisterClient() {
        if (!isClientRegistered()) {
            throw new IllegalStateException("Cannot deregister client, because client is not registered.");
        }

        centralHub.deregisterClient(agent.getClientNodeAddress());
        setClientNodeAddress(-1);
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
     * Requests the change of state for an actuator on a remote field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress  the address of the actuator
     * @param newState         the new state to set
     */
    public void requestActuatorActivationForFieldNode(int fieldNodeAddress, int actuatorAddress, int newState) throws IOException {
        FieldNodeClientProxy proxy = centralHub.getFieldNodeProxy(fieldNodeAddress);
        // checks if a field node with the given address exists
        if (proxy != null) {
            sendFieldNodeActuatorRequest(proxy, actuatorAddress, newState);
        } else {
            throw new IOException("Cannot request actuator activation for field node with" +
                    " address " + fieldNodeAddress + ", because no such field node exists.");
        }
    }

    /**
     * Sends a request to activate a specific actuator on a given field node.
     *
     * @param proxy           the field node proxy to send request to
     * @param actuatorAddress the address of the actuator to activate
     * @param newState        the new state to set for the actuator
     * @throws IOException thrown if an I/O exception occurs
     */
    private static void sendFieldNodeActuatorRequest(FieldNodeClientProxy proxy, int actuatorAddress, int newState) throws IOException {
        // checks if the field node has an actuator with the given address
        if (fieldNodeHasActuator(actuatorAddress, proxy)) {
            ControlCommAgent agent = proxy.getAgent();
            agent.sendRequest(new FieldNodeActivateActuatorRequest(actuatorAddress, newState));
        } else {
            throw new IOException("Cannot request actuator activation for actuator " +
                    "with address " + actuatorAddress + " on field node, because no such actuator exists.");
        }
    }

    /**
     * Returns whether a locally stored field node proxy owns an actuator with a specific address.
     *
     * @param actuatorAddress the address of the actuator
     * @param proxy           the field node proxy to check for
     * @return true if field node proxy has actuator, false otherwise
     */
    private static boolean fieldNodeHasActuator(int actuatorAddress, FieldNodeClientProxy proxy) {
        return proxy.getFNSM().containsKey(actuatorAddress);
    }

    /**
     * Updates the address for a given actuator for a given field node.
     * The information is updated locally at the central server, and does not provoke a request to the actual field
     * node.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState        the new state to set
     * @throws NoSuchAddressException thrown if one of the addresses is invalid
     */
    public void updateLocalActuatorState(int actuatorAddress, int newState) throws NoSuchAddressException {
        centralHub.setLocalActuatorState(agent.getClientNodeAddress(), actuatorAddress, newState);
    }

    /**
     * Updates the local ADL for the field node.
     *
     * @param updatedAdl the updated adl
     */
    public void updateLocalAdl(Set<Integer> updatedAdl) {
        centralHub.updateLocalAdl(agent.getClientNodeAddress(), updatedAdl);
    }

    @Override
    public void logReceivingRequest(RequestMessage request) {
        loggers.forEach(logger -> logger.logInfo(ServerEventFormatter.requestReceived(request, agent.getRemoteEntityAsString())));
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        loggers.forEach(logger -> logger.logInfo(ServerEventFormatter.responseReceived(response, agent.getRemoteEntityAsString())));
    }
}
