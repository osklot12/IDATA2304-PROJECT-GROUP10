package no.ntnu.network.centralserver.centralhub;

import no.ntnu.exception.*;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.centralserver.centralhub.clientproxy.ClientProxy;
import no.ntnu.network.centralserver.centralhub.clientproxy.ControlPanelClientProxy;
import no.ntnu.network.centralserver.centralhub.clientproxy.FieldNodeClientProxy;
import no.ntnu.network.connectionservice.sensordatarouter.SensorDataDestination;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.request.AdlUpdateRequest;
import no.ntnu.network.message.request.ServerFnsmNotificationRequest;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.representation.FieldNodeInformation;
import no.ntnu.tools.logger.SimpleLogger;

import java.io.IOException;
import java.util.*;

/**
 * The CentralHub is the 'logic class' for the central server, responsible for managing clients.
 * Although the class does handle client communication, it is not dependent on a concrete communication implementation,
 * and can therefore handle client communication of any type.
 */
public class CentralHub implements SensorDataDestination, DeviceLookupTable {
    private final Map<Integer, FieldNodeClientProxy> fieldNodes;
    private final Map<Integer, ControlPanelClientProxy> controlPanels;
    private final Map<Integer, Set<Integer>> sensorDataRoutingTable;
    private final Set<SimpleLogger> loggers;

    /**
     * Creates a new CentralHub.
     */
    public CentralHub() {
        this.fieldNodes = new HashMap<>();
        this.controlPanels = new HashMap<>();
        this.sensorDataRoutingTable = new HashMap<>();
        this.loggers = new HashSet<>();
    }

    /**
     * Adds a logger to the central hub.
     *
     * @param logger the logger to add
     */
    public void addLogger(SimpleLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Cannot add logger, because logger is null.");
        }

        loggers.add(logger);
    }

    /**
     * Logs an error.
     *
     * @param message the error message to log
     */
    private void logError(String message) {
        loggers.forEach(logger -> logger.logError(message));
    }

    /**
     * Registers a field node client.
     *
     * @param fieldNodeInformation information about the field node
     * @param agent                the associated communication agent
     * @return the assigned address for the field node
     * @throws ClientRegistrationException thrown if registration fails
     */
    public synchronized int registerFieldNode(FieldNodeInformation fieldNodeInformation, ControlCommAgent agent) throws ClientRegistrationException {
        if (fieldNodeInformation == null) {
            throw new IllegalArgumentException("Cannot register field node, because fieldNodeInformation is null.");
        }

        if (agent == null) {
            throw new IllegalArgumentException("Cannot register field node, because agent is null.");
        }

        FieldNodeClientProxy clientProxy = new FieldNodeClientProxy(agent, fieldNodeInformation);
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
     * @param controlAgent      the control communication agent for the control panel client
     * @param dataAgent         the sensor data communication agent for the control panel client
     * @return the assigned address for the control panel
     * @throws ClientRegistrationException thrown if registration fails
     */
    public synchronized int registerControlPanel(Set<DeviceClass> compatibilityList, ControlCommAgent controlAgent, DataCommAgent dataAgent) throws ClientRegistrationException {
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
     * Deregisters a client.
     *
     * @param clientAddress the client address
     */
    public synchronized void deregisterClient(int clientAddress) {
        if (fieldNodes.remove(clientAddress) != null) {
            handleFieldNodeRemoval(clientAddress);
        } else if (controlPanels.remove(clientAddress) != null) {
            removeSensorDataSubscriber(clientAddress);
        }
    }

    /**
     * Handles the removal of a field node.
     *
     * @param clientAddress the address of the field node
     */
    private void handleFieldNodeRemoval(int clientAddress) {
        sensorDataRoutingTable.remove(clientAddress);
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
     * Returns the control panel proxy for with a given address.
     *
     * @param address the address of the control panel proxy
     * @return the control panel proxy, null if no control panel with the provided address exists
     */
    public ControlPanelClientProxy getControlPanelProxy(int address) {
        return controlPanels.get(address);
    }

    /**
     * Returns the field node client proxy with a given address.
     *
     * @param address the address of the field node proxy
     * @return the field node proxy, null if no field node with the provided address exists
     */
    public FieldNodeClientProxy getFieldNodeClientProxy(int address) {
        return fieldNodes.get(address);
    }

    /**
     * Subscribes a control panel to a field node.
     *
     * @param subscriber       the communication agent for the control panel
     * @param fieldNodeAddress the node address of the field node to subscribe to
     * @return an Optional containing the corresponding field node client proxy, or empty if subscription fails
     * @throws SubscriptionException if subscribing fails
     */
    public synchronized FieldNodeClientProxy subscribeToFieldNode(ControlCommAgent subscriber, int fieldNodeAddress) throws SubscriptionException {
        Objects.requireNonNull(subscriber, "Cannot subscribe to field node because subscriber is null.");

        if (!sensorDataRoutingTable.containsKey(fieldNodeAddress)) {
            throw new SubscriptionException(formatExceptionMessage("no such field node exists", fieldNodeAddress));
        }

        return handleFieldNodeSubscription(subscriber.getClientNodeAddress(), fieldNodeAddress);
    }

    /**
     * Handles the subscription to a field node.
     * Only control panels can currently subscribe to field nodes.
     *
     * @param subscriberAddress the node address of the subscriber
     * @param fieldNodeAddress  the node address of the field node to subscribe to
     * @return the client proxy for the field node, or null if subscription is not successful
     * @throws SubscriptionException if subscription fails
     */
    private FieldNodeClientProxy handleFieldNodeSubscription(int subscriberAddress, int fieldNodeAddress) throws SubscriptionException {
        if (!validControlPanel(subscriberAddress)) {
            throw new SubscriptionException(formatExceptionMessage("control panel is not registered", fieldNodeAddress));
        }

        if (getFieldNodeSubscribers(fieldNodeAddress).contains(subscriberAddress)) {
            throw new SubscriptionException(formatExceptionMessage("control panel is already subscribed", fieldNodeAddress));
        }

        addFieldNodeSubscriber(subscriberAddress, fieldNodeAddress);
        if (adlOutOfSync(fieldNodeAddress)) {
            try {
                sendAdlUpdate(fieldNodeAddress);
            } catch (IOException e) {
                throw new SubscriptionException(e.getMessage());
            }
        }

        return fieldNodes.get(fieldNodeAddress);
    }

    /**
     * Indicates whether the local ADL for a field node contains data that the field node does not have.
     *
     * @param fieldNodeAddress the address of the field node
     * @return true if field node knows about adl changes, false otherwise
     */
    private boolean adlOutOfSync(int fieldNodeAddress) {
        boolean isSynchronized = false;

        FieldNodeClientProxy fieldNodeProxy = fieldNodes.get(fieldNodeAddress);
        if (fieldNodeProxy != null) {
            isSynchronized = fieldNodeProxy.getAdl().equals(getAdlForFieldNode(fieldNodeAddress));
        } else {
            throw new IllegalArgumentException("Cannot check if adl is synchronized, because no field node with" +
                    " address " + fieldNodeAddress + " was found.");
        }

        return !isSynchronized;
    }

    private boolean validControlPanel(int subscriberAddress) {
        return controlPanels.containsKey(subscriberAddress);
    }

    private String formatExceptionMessage(String reason, int fieldNodeAddress) {
        return String.format("Cannot subscribe to field node with address %d, because %s.", fieldNodeAddress, reason);
    }

    /**
     * Unsubscribes a control panel from a field node.
     *
     * @param subscriber       the communication agent for the control panel
     * @param fieldNodeAddress the node address of the field node to unsubscribe from
     * @throws SubscriptionException thrown if unsubscribing fails
     */
    public void unsubscribeFromFieldNode(ControlCommAgent subscriber, int fieldNodeAddress) throws SubscriptionException {
        if (subscriber == null) {
            throw new IllegalArgumentException("Cannot unsubscribe from field node with address " + fieldNodeAddress +
                    ", because subscriber is null.");
        }

        Set<Integer> subscribers = getFieldNodeSubscribers(fieldNodeAddress);
        int subscriberAddress = subscriber.getClientNodeAddress();
        if (subscribers != null && subscribers.contains(subscriberAddress)) {
            subscribers.remove(subscriberAddress);
            if (adlOutOfSync(fieldNodeAddress)) {
                try {
                    sendAdlUpdate(fieldNodeAddress);
                } catch (IOException e) {
                    throw new SubscriptionException(e.getMessage());
                }
            }
        } else {
            throw new SubscriptionException("Cannot unsubscribe from field node " + fieldNodeAddress + ", because " +
                    "no such subscription exists.");
        }
    }

    /**
     * Updates the locally stored ADL for a field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @param updatedAdl       the updated adl to set
     */
    public synchronized void updateLocalAdl(int fieldNodeAddress, Set<Integer> updatedAdl) {
        if (updatedAdl == null) {
            throw new IllegalArgumentException("Cannot update local ADL, because updatedAdl is null.");
        }

        fieldNodes.get(fieldNodeAddress).setAdl(updatedAdl);
    }

    /**
     * Removes a client completely from the sensor data routing table.
     *
     * @param subscriberAddress the subscriber to remove
     */
    public synchronized void removeSensorDataSubscriber(int subscriberAddress) {
        sensorDataRoutingTable.values().forEach(set -> set.remove(subscriberAddress));
    }

    /**
     * Adds a subscriber to a field node.
     *
     * @param subscriberAddress the address of the subscribing control panel
     * @param fieldNodeAddress  the address of the field node to subscribe to
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
    public Set<Integer> getFieldNodeSubscribers(int fieldNodeAddress) {
        return sensorDataRoutingTable.get(fieldNodeAddress);
    }

    /**
     * Generates and sends an ADL update to the given field node address.
     *
     * @param fieldNodeAddress the field node to send update to
     */
    private void sendAdlUpdate(int fieldNodeAddress) throws IOException {
        ControlCommAgent fieldNodeAgent = fieldNodes.get(fieldNodeAddress).getAgent();
        fieldNodeAgent.sendRequest(new AdlUpdateRequest(getAdlUpdate(fieldNodeAddress)));
    }

    /**
     * Returns an ADL update for a field node.
     * An ADL update is a set of device classes, where positive addresses means the adding of the device to the ADL,
     * while negative addresses means the removal of the device from the ADL.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the adl update
     */
    private Set<Integer> getAdlUpdate(int fieldNodeAddress) {
        Set<Integer> previousAdl = fieldNodes.get(fieldNodeAddress).getAdl();
        Set<Integer> newAdl = getAdlForFieldNode(fieldNodeAddress);

        // computes the addresses to add - the ones that are in the new adl but not in the previous
        Set<Integer> addressesToAdd = new HashSet<>(newAdl);
        addressesToAdd.removeAll(previousAdl);

        // computes the addresses to remove - the ones that were in the previous adl but not in the new
        Set<Integer> addressesToRemove = new HashSet<>(previousAdl);
        addressesToRemove.removeAll(newAdl);

        // add addresses to add to the adl update
        Set<Integer> adlUpdate = new HashSet<>(addressesToAdd);

        // negate the addresses to remove and add them to update
        addressesToRemove.forEach(address -> adlUpdate.add(address * -1));

        return adlUpdate;
    }

    /**
     * Generates the current ADL for a given field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @return the generated adl
     */
    private Set<Integer> getAdlForFieldNode(int fieldNodeAddress) {
        return generateAdl(fieldNodeAddress, getActiveDeviceClasses(fieldNodeAddress));
    }

    /**
     * Generates a current ADL for a field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @param activeClasses    the active device classes for the field node
     * @return a current ADL for the field node
     */
    private Set<Integer> generateAdl(int fieldNodeAddress, Set<DeviceClass> activeClasses) {
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
     * Returns the active device classes for a given field node.
     * A device class is considered active for a field node if a subscriber is compatible with this device class, and
     * the field node owns a device of this kind.
     *
     * @param fieldNodeAddress the address of the field node
     * @return a set of all active device classes for the field node
     */
    private Set<DeviceClass> getActiveDeviceClasses(int fieldNodeAddress) {
        Set<DeviceClass> activeClasses = new HashSet<>();

        Set<Integer> subscribers = getFieldNodeSubscribers(fieldNodeAddress);
        subscribers.forEach(subscriber -> {
            // adding all compatible device classes for each subscriber of the field node
            ControlPanelClientProxy controlPanel = controlPanels.get(subscriber);
            activeClasses.addAll(controlPanel.getCompatibilityList());
        });

        return activeClasses;
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
     * @param actuatorAddress  the address of the actuator
     * @param newState         the new state to set
     * @throws NoSuchAddressException thrown if one of the addresses is invalid
     */
    public synchronized void setLocalActuatorState(int fieldNodeAddress, int actuatorAddress, int newState) throws NoSuchAddressException {
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
     * @param actuatorAddress  the address of the actuator
     * @param newState         the new state of the actuator
     */
    private void handleActuatorStateChangeForwarding(int fieldNodeAddress, int actuatorAddress, int newState) {
        ServerFnsmNotificationRequest request = new ServerFnsmNotificationRequest(fieldNodeAddress, actuatorAddress, newState);

        getFieldNodeSubscribers(fieldNodeAddress).forEach(controlPanelAddress -> {
            ControlPanelClientProxy controlPanel = controlPanels.get(controlPanelAddress);
            ControlCommAgent agent = controlPanel.getAgent();

            try {
                agent.sendRequest(request);
            } catch (IOException e) {
                logError("Cannot notify " + agent.getRemoteEntityAsString() + " about the change of state for" +
                        "actuator " + actuatorAddress + " on field node " + fieldNodeAddress);
            }
        });
    }

    @Override
    public DeviceClass lookup(int clientAddress, int deviceAddress) {
        return fieldNodes.get(clientAddress).getFNST().get(deviceAddress);
    }

    @Override
    public void receiveSensorData(SensorDataMessage sensorData) {
        // further routes the sensor data to the subscribed control panels
        Set<Integer> subscribers = getFieldNodeSubscribers(sensorData.getClientNodeAddress());

        subscribers.forEach(subscriberAddress -> {
            ControlPanelClientProxy controlPanel = controlPanels.get(subscriberAddress);
            try {
                controlPanel.sendSensorData(sensorData);
            } catch (IOException e) {
                logError("Cannot send sensor data to control panel with address " + subscriberAddress + ": " +
                        e.getMessage());
            }
        });
    }
}
