package no.ntnu.controlpanel;

import no.ntnu.controlpanel.virtual.*;
import no.ntnu.controlpanel.virtual.actuator.VirtualStandardActuator;
import no.ntnu.fieldnode.FieldNodeListener;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.representation.FieldNodeAgent;
import no.ntnu.network.connectionservice.sensordatarouter.SensorDataDestination;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.sensordata.SensorDataReceiver;
import no.ntnu.tools.SystemOutLogger;

import java.util.*;

/**
 * A class representing a control panel.
 * The control panel is responsible for representing and managing remote field nodes.
 */
public class ControlPanel implements SensorDataDestination, SensorDataReceiver, VirtualFieldNodeListener, DeviceLookupTable {
    private final Map<Integer, VirtualFieldNode> fieldNodes;
    private final Set<ControlPanelListener> listeners;
    private final Set<DeviceClass> compatibilityList;
    private FieldNodeAgent fieldNodeAgent; // the interface for interacting with remote field nodes
    private Map<Integer, String> fieldNodePool;

    /**
     * Creates a new ControlPanel.
     */
    public ControlPanel() {
        this.compatibilityList = CompatibilityListCreator.getCompleteCompatibilityList();
        this.listeners = new HashSet<>();
        this.fieldNodes = new HashMap<>();
        this.fieldNodePool = new HashMap<>();
    }

    /**
     * Sets the field node information agent for the control panel.
     *
     * @param fieldNodeAgent the agent responsible for providing field node information
     */
    public void setFieldNodeAgent(FieldNodeAgent fieldNodeAgent) {
        if (fieldNodeAgent == null) {
            throw new IllegalArgumentException("Cannot create ControlPanel, because fieldNodeINfoAgent is null.");
        }

        this.fieldNodeAgent = fieldNodeAgent;
    }

    /**
     * Returns the field node agent for the control panel.
     *
     * @return the field node agent
     */
    public FieldNodeAgent getFieldNodeAgent() {
        if (fieldNodeAgent == null) {
            throw new IllegalStateException("Cannot access the field node agent, because it is not yet set.");
        }

        return fieldNodeAgent;
    }

    /**
     * Adds a listener to listen for events for the control panel.
     *
     * @param listener the listener to add
     */
    public void addListener(ControlPanelListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds a new virtual field node to the control panel.
     *
     * @param fieldNode virtual field node to add
     * @param address the address of the field node
     */
    public void addVirtualFieldNode(VirtualFieldNode fieldNode, int address) {
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot add virtual field node, because fieldNode is null.");
        }

        if (fieldNodes.containsKey(address)) {
            throw new IllegalArgumentException("Cannot add virtual field node, because a virtual field node with" +
                    "address " + address + " already exists.");
        }

        fieldNodes.put(address, fieldNode);
        fieldNode.addListener(address, this);
        listeners.forEach(listener -> listener.fieldNodeAdded(address));
    }

    /**
     * Removes a virtual field node.
     *
     * @param address the address of the virtual field node to remove
     */
    public void removeVirtualFieldNode(int address) {
        fieldNodes.remove(address);
        listeners.forEach(listener -> listener.fieldNodeRemoved(address));
    }

    /**
     * Returns the field node pool.
     *
     * @return the field node pool
     */
    public Map<Integer, String> getFieldNodePool() {
        return fieldNodePool;
    }

    /**
     * Adds a field node pool to the control panel.
     *
     * @param fieldNodePool the field node pool to add
     */
    public void setFieldNodePool(Map<Integer, String> fieldNodePool) {
        if (fieldNodePool == null) {
            throw new IllegalArgumentException("Cannot set field node pool, because fieldNodePool is null.");
        }

        this.fieldNodePool = fieldNodePool;
        listeners.forEach(listener -> listener.fieldNodePoolReceived(this));
    }

    /**
     * Returns the field node with a given address.
     *
     * @param address the address for the field node
     * @return the virtual field node with the given address, null if no such virtual field node exists
     */
    public VirtualFieldNode getVirtualFieldNode(int address) {
        return fieldNodes.get(address);
    }

    /**
     * Returns a collection of all virtual field nodes for the control panel.
     *
     * @return collection of virtual field nodes
     */
    public Collection<VirtualFieldNode> getVirtualFieldNodes() {
        return fieldNodes.values();
    }

    /**
     * Returns the compatibility list for the control panel.
     *
     * @return the compatibility list
     */
    public Set<DeviceClass> getCompatibilityList() {
        return compatibilityList;
    }

    /**
     * Returns a string representation of the source of field nodes.
     *
     * @return the string representation, null if no field node source is set
     */
    public String getFieldNodeSourceAsString() {
        if (fieldNodeAgent == null) {
            return null;
        }

        return fieldNodeAgent.getFieldNodeSourceAsString();
    }

    @Override
    public DeviceClass lookup(int clientAddress, int deviceAddress) {
        DeviceClass deviceClass = null;

        VirtualFieldNode virtualFieldNode = getVirtualFieldNode(clientAddress);
        if (virtualFieldNode != null) {
            VirtualDevice virtualDevice = virtualFieldNode.getVirtualDevice(deviceAddress);

            if (virtualDevice != null) {
                deviceClass = virtualDevice.getDeviceClass();
            }
        }

        return deviceClass;
    }

    @Override
    public void receiveSduData(int fieldNodeAddress, int sensorAddress, double data) {
        VirtualFieldNode virtualFieldNode = fieldNodes.get(fieldNodeAddress);
        if (virtualFieldNode != null) {
            virtualFieldNode.addSDUSensorData(sensorAddress, data);
        }
    }

    @Override
    public void receiveSensorData(SensorDataMessage sensorData) {
        sensorData.extractData(this);
    }

    @Override
    public void virtualActuatorStateChanged(int fieldNodeAddress, int actuatorAddress) {
        VirtualFieldNode fieldNode = getVirtualFieldNode(fieldNodeAddress);
        if (fieldNode != null) {
            VirtualStandardActuator actuator = fieldNode.getVirtualStandardActuator(actuatorAddress);
            if (actuator != null) {
                int state = actuator.getState();
                getFieldNodeAgent().setActuatorState(fieldNodeAddress, actuatorAddress, state);
            }
        }
    }
}
