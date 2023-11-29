package no.ntnu.controlpanel;

import no.ntnu.controlpanel.virtual.*;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.deserialize.component.DeviceLookupTable;
import no.ntnu.network.message.sensordata.SensorDataReceiver;

import java.util.*;

/**
 * A class representing a control panel.
 * The control panel stores data about field nodes, such as the sensor data they capture or the status of the
 * actuators.
 */
public class ControlPanel implements SensorDataReceiver, VirtualFieldNodeListener, DeviceLookupTable {
    private final Map<Integer, VirtualFieldNode> fieldNodes;
    private Set<DeviceClass> compatibilityList = new HashSet<>();

    /**
     * Creates a new ControlPanel.
     */
    public ControlPanel() {
        this.compatibilityList = CompatibilityListCreator.getCompleteCompatibilityList();
        this.fieldNodes = new HashMap<>();
    }

    /**
     * Adds a new virtual field node to the control panel.
     *
     * @param fieldNode virtual field node to add
     * @param address the address pf the field node
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
    }

    /**
     * Removes a virtual field node.
     *
     * @param fieldNodeAddress the address of the virtual field node to remove
     */
    public void removeVirtualFieldNode(int fieldNodeAddress) {
        fieldNodes.remove(fieldNodeAddress);
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

    @Override
    public void virtualActuatorStateChanged(VirtualFieldNode fieldNode, int actuatorAddress, boolean global) {

    }

    @Override
    public void newSDUData(VirtualSDUSensor virtualSDUSensor) {

    }

    @Override
    public void receiveSduData(int fieldNodeAddress, int sensorAddress, double data) {
        VirtualFieldNode virtualFieldNode = fieldNodes.get(fieldNodeAddress);
        if (virtualFieldNode != null) {
            virtualFieldNode.addSDUSensorData(sensorAddress, data);
        }
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
}
