package no.ntnu.network.centralserver.centralhub;

import no.ntnu.exception.*;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.centralserver.centralhub.clientproxy.ClientProxy;
import no.ntnu.network.centralserver.centralhub.clientproxy.ControlPanelClientProxy;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.request.AdlUpdateRequest;
import no.ntnu.network.message.request.ServerFnsmNotificationRequest;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The CentralHub is the 'logic class' for the central server, responsible for managing clients.
 * Although the class does handle client communication, it is not dependent on a concrete communication implementation,
 * and can therefore handle client communication of any type.
 */
public class CentralHub implements DeviceLookupTable {
    private final Map<Integer, FieldNodeClientProxy> fieldNodes;
    private final Map<Integer, ControlPanelClientProxy> controlPanels;
    private final Map<Integer, Set<Integer>> sensorDataRoutingTable;

    /**
     * Creates a new CentralHub.
     */
    public CentralHub() {
        this.fieldNodes = new HashMap<>();
        this.controlPanels = new HashMap<>();
        this.sensorDataRoutingTable = new HashMap<>();
    }

    /**
     * Registers a field node client.
     *
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name the name of the field node
     * @param agent the associated communication agent
     * @return the assigned address for the field node
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerFieldNode(Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name, ControlCommAgent agent) throws ClientRegistrationException {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot register field node, because fnst is null.");
        }

        if (fnsm == null) {
            throw new IllegalArgumentException("Cannot register field node, because fnsm is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot register field node, because name is null.");
        }

        if (agent == null) {
            throw new IllegalArgumentException("Cannot register field node, because agent is null.");
        }

        FieldNodeClientProxy clientProxy = new FieldNodeClientProxy(agent, fnst, fnsm, name);
        int clientAddress = registerClient(clientProxy, fieldNodes);
        // checks if address is -1, which indicated that the client is already registered
        if (clientAddress == -1) {
            throw new ClientRegistrationException("Cannot register field node, because it is already registered.");
        }

        // adds a new entry to the sensor data routing table
        sensorDataRoutingTable.put(clientAddress, new HashSet<>());

        return clientAddress;
    }

    /**
     * Registers a control panel client.
     *
     * @param compatibilityList the compatibility list
     * @param controlAgent the control communication agent for the control panel client
     * @param dataAgent the sensor data communication agent for the control panel client
     * @return the assigned address for the control panel
     * @throws ClientRegistrationException thrown if registration fails
     */
    public int registerControlPanel(Set<DeviceClass> compatibilityList, ControlCommAgent controlAgent, DataCommAgent dataAgent) throws ClientRegistrationException {
        if (compatibilityList == null) {
            throw new IllegalArgumentException("Cannot register control panel, because compatibility list is null.");
        }

        if (controlAgent == null) {
            throw new IllegalArgumentException("Cannot register control panel, because agent is null.");
        }

        ControlPanelClientProxy clientProxy = new ControlPanelClientProxy(controlAgent, dataAgent, compatibilityList);
        int clientAddress = registerClient(clientProxy, controlPanels);
        // checks if address is -1, which indicates that the client is already registered
        if (clientAddress == -1) {
            throw new ClientRegistrationException("Cannot register control panel, because it is already registered.");
        }

        return clientAddress;
    }

    /**
     * Registers a client in a register.
     *
     * @param client   the client to register
     * @param register the register in which to put the client
     * @param <C>      the client proxy
     * @return the assigned address for the client, -1 if client is already registered
     * @throws ClientRegistrationException thrown if client cannot be registered
     */
    private synchronized <C extends ClientProxy> int registerClient(C client, Map<Integer, C> register) {
        int clientAddress = -1;

        if (!(register.containsValue(client))) {
            clientAddress = generateNewClientAddress();
            register.put(clientAddress, client);
        }

        return clientAddress;
    }

    /**
     * Generates the smallest available address in a map of addresses.
     *
     * @return the smallest available address
     */
    private int generateNewClientAddress() {
        int currentCheck = 0;

        Set<Integer> addresses = getAllAddresses();
        while (addresses.contains(currentCheck)) {
            currentCheck++;
        }

        return currentCheck;
    }

    /**
     * Returns a set of all client addresses currently in use.
     *
     * @return all client addresses
     */
    private Set<Integer> getAllAddresses() {
        Set<Integer> allAddresses = new HashSet<>();
        allAddresses.addAll(fieldNodes.keySet());
        allAddresses.addAll(controlPanels.keySet());

        return allAddresses;
    }

    /**
     * Subscribes a control panel to a field node.
     *
     * @param subscriber the communication agent for the control panel
     * @param fieldNodeAddress the node address of the field node to subscribe to
     * @return the corresponding field node client proxy
     * @throws SubscriptionException thrown if subscribing fails
     */
    public FieldNodeClientProxy subscribeToFieldNode(ControlCommAgent subscriber, int fieldNodeAddress) throws SubscriptionException{
        if (subscriber == null) {
            throw new IllegalArgumentException("Cannot subscribe to field node because subscriber is null.");
        }

        FieldNodeClientProxy fieldNodeProxy = null;
        int subscriberAddress = subscriber.getClientNodeAddress();
        // checks if the given field node address is valid
        if (sensorDataRoutingTable.containsKey(fieldNodeAddress)) {
            fieldNodeProxy = handleFieldNodeSubscription(subscriberAddress, fieldNodeAddress);
        } else {
            throw new SubscriptionException("Cannot subscribe to field node with address " + fieldNodeAddress +
                    ", because no such field node exists.");
        }

        return fieldNodeProxy;
    }

    /**
     * Handles the subscription to a field node.
     * As for now, control panels are the only clients that can subscribe to field nodes.
     * This method can be modified to allow new clients to also subscribe to fields nodes.
     *
     * @param subscriberAddress the node address of the subscriber
     * @param fieldNodeAddress the node address of the field node to subscribe to
     * @return the client proxy for the field node subscribed to
     * @throws SubscriptionException thrown if subscription fails
     */
    private FieldNodeClientProxy handleFieldNodeSubscription(int subscriberAddress, int fieldNodeAddress) throws SubscriptionException {
        FieldNodeClientProxy fieldNodeProxy = null;

        // checks if the control panel is registered
        if (controlPanels.containsKey(subscriberAddress)) {

            // checks if control panel is already subscribed
            if (getFieldNodeSubscribers(fieldNodeAddress).contains(subscriberAddress)) {
                throw new SubscriptionException("Cannot subscribe to field node with address " + fieldNodeAddress +
                        ", because control panel is already subscribed.");
            }

            Set<Integer> previousAdl = getAdlForFieldNode(fieldNodeAddress);

            // handles the routing configuration
            addFieldNodeSubscriber(subscriberAddress, fieldNodeAddress);

            Set<Integer> newAdl = getAdlForFieldNode(fieldNodeAddress);
            // only trigger adl update if adl changes
            if (!previousAdl.equals(newAdl)) {
                try {
                    sendAdlUpdate(fieldNodeAddress);
                } catch (IOException e) {
                    // ensuring atomicity principle by removing subscriber in case of error
                    removeFieldNodeSubscriber(subscriberAddress, fieldNodeAddress);
                    throw new SubscriptionException("Cannot subscribe to field node with address " + fieldNodeAddress +
                            ", because ADL update failed to send: " + e.getMessage());
                }
            }
            fieldNodeProxy = fieldNodes.get(fieldNodeAddress);
        } else {
            throw new SubscriptionException("Cannot subscribe to field node with address " + fieldNodeAddress +
                    ", because the control panel is not registered.");
        }

        return fieldNodeProxy;
    }

    /**
     * Unsubscribes a control panel from a field node.
     *
     * @param subscriber the communication agent for the control panel
     * @param fieldNodeAddress the node address of the field node to unsubscribe from
     * @throws SubscriptionException thrown if unsubscribing fails
     */
    public void unsubscribeFromFieldNode(ControlCommAgent subscriber, int fieldNodeAddress) throws SubscriptionException {
        if (subscriber == null) {
            throw new IllegalArgumentException("Cannot unsubscribe from field node with address "+ fieldNodeAddress +
                    ", because subscriber is null.");
        }

        Set<Integer> subscribers = getFieldNodeSubscribers(fieldNodeAddress);
        int subscriberAddress = subscriber.getClientNodeAddress();
        if (subscribers != null && subscribers.contains(subscriberAddress)) {
             subscribers.remove(subscriberAddress);
        } else {
            throw new SubscriptionException("Cannot unsubscribe from field node " + fieldNodeAddress + ", because " +
                    "no such subscription exists.");
        }
    }

    /**
     * Adds a subscriber to a field node.
     *
     * @param subscriberAddress the address of the subscribing control panel
     * @param fieldNodeAddress the address of the field node to subscribe to
     */
    private void addFieldNodeSubscriber(int subscriberAddress, int fieldNodeAddress) {
        Set<Integer> subscribers = getFieldNodeSubscribers(fieldNodeAddress);
        subscribers.add(subscriberAddress);
    }

    /**
     * Returns a set of addresses representing the control panels subscribed to the given field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the field node subscribers
     */
    private Set<Integer> getFieldNodeSubscribers(int fieldNodeAddress) {
        return sensorDataRoutingTable.get(fieldNodeAddress);
    }

    /**
     * Removes a subscriber of a field node.
     *
     * @param subscriberAddress the address of the unsubscribing control panel
     * @param fieldNodeAddress the address of the field node to unsubscribe to
     */
    private void removeFieldNodeSubscriber(int subscriberAddress, int fieldNodeAddress) {
        Set<Integer> subscribers = sensorDataRoutingTable.get(fieldNodeAddress);
        subscribers.remove(subscriberAddress);
    }

    /**
     * Generates and sends an ADL update to the given field node address.
     *
     * @param fieldNodeAddress the field node to send update to
     */
    private void sendAdlUpdate(int fieldNodeAddress) throws IOException {
        Set<Integer> adl = getAdlForFieldNode(fieldNodeAddress);
        ControlCommAgent fieldNodeAgent = fieldNodes.get(fieldNodeAddress).getAgent();
        fieldNodeAgent.sendRequest(new AdlUpdateRequest(adl));
    }

    /**
     * Generates an ADL for a given field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the adl
     */
    private Set<Integer> getAdlForFieldNode(int fieldNodeAddress) {
        Set<DeviceClass> activeClasses = new HashSet<>();
        Set<Integer> subscribers = sensorDataRoutingTable.get(fieldNodeAddress);
        subscribers.forEach(subscriber -> {
            // adding all compatible device classes for each subscriber of the field node
            ControlPanelClientProxy controlPanel = controlPanels.get(subscriber);
            activeClasses.addAll(controlPanel.getCompatibilityList());
        });

        Set<Integer> adl = new HashSet<>();
        Map<Integer, DeviceClass> fnst = fieldNodes.get(fieldNodeAddress).getFNST();
        fnst.forEach((key, value) -> {
            // adding the device address to adl if the device class is 'active'
            if (activeClasses.contains(value)) {
                adl.add(key);
            }
        });

        return adl;
    }

    /**
     * Returns the field node pool.
     *
     * @return the field node pool
     */
    public Map<Integer, String> getFieldNodePool() {
        Map<Integer, String> fieldNodePool = new HashMap<>();

        fieldNodes.forEach((key, value) -> fieldNodePool.put(key, value.getName()));

        return fieldNodePool;
    }

    /**
     * Sets the local state for a given actuator.
     * This makes the central hub update its FNSM for the appropriate field node, and will notify any subscribed
     * control panel about the event.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state to set
     * @throws NoSuchAddressException thrown if one of the addresses is invalid
     */
    public void setLocalActuatorState(int fieldNodeAddress, int actuatorAddress, int newState) throws NoSuchAddressException {
        if (!(fieldNodes.containsKey(fieldNodeAddress))) {
            throw new NoSuchAddressException("Cannot set actuator state for field node with address " + fieldNodeAddress +
                    ", because no such field node exists.");
        }

        FieldNodeClientProxy fieldNode = fieldNodes.get(fieldNodeAddress);
        try {
            fieldNode.setActuatorState(actuatorAddress, newState);
            handleActuatorStateChangeForwarding(fieldNodeAddress, actuatorAddress, newState);
        } catch (NoSuchActuatorException e) {
            throw new NoSuchAddressException("Cannot set state of actuator with address " + actuatorAddress +
                    " for field node with address " + fieldNodeAddress + ", because no such actuator exists.");
        }
    }

    /**
     * Returns the field node proxy with the given address.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the field node proxy for the given address, null if none is found
     */
    public FieldNodeClientProxy getFieldNodeProxy(int fieldNodeAddress) {
        return fieldNodes.get(fieldNodeAddress);
    }

    /**
     * Handles the forwarding of information about the change of state for an actuator.
     * All control panels subscribed to the field node will need to be notified about this event.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    private void handleActuatorStateChangeForwarding(int fieldNodeAddress, int actuatorAddress, int newState) {
        ServerFnsmNotificationRequest request = new ServerFnsmNotificationRequest(fieldNodeAddress, actuatorAddress, newState);

        getFieldNodeSubscribers(fieldNodeAddress).forEach(controlPanelAddress -> {
            ControlPanelClientProxy controlPanel = controlPanels.get(controlPanelAddress);
            ControlCommAgent agent = controlPanel.getAgent();

            try {
                agent.sendRequest(request);
            } catch (IOException e) {
                Logger.error("Cannot notify " + agent.getRemoteEntityAsString() + " about the change of state for" +
                        "actuator " + actuatorAddress + " on field node " + fieldNodeAddress);
            }
        });
    }

    @Override
    public DeviceClass lookup(int clientAddress, int deviceAddress) {
        return fieldNodes.get(clientAddress).getFNST().get(deviceAddress);
    }

    /**
     * Routes the sensor data message to the subscribers of the source client.
     *
     * @param message the sensor data message to route
     */
    public void routeSensorDataToSubscribers(SensorDataMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot route sensor data message, because message is null.");
        }

        Set<Integer> subscribers = getFieldNodeSubscribers(message.getClientNodeAddress());
        subscribers.forEach(subscriberAddress -> {
            ControlPanelClientProxy controlPanel = controlPanels.get(subscriberAddress);
            try {
                controlPanel.sendSensorData(message);
            } catch (IOException e) {
                Logger.error("Cannot send sensor data to control panel with address " + subscriberAddress + ": " +
                        e.getMessage());
            }
        });
    }
}
