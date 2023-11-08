package no.ntnu.controlpanel;

import no.ntnu.controlpanel.virtual.*;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.*;

/**
 * A class responsible for the logic of the control panel in the network.
 * The control panel stores data about field nodes, such as the sensor data they capture or the status of the
 * actuators.
 */
public class ControlPanel implements VirtualFieldNodeListener {
    private static final Set<DeviceClass> COMPATIBILITY_LIST = new HashSet<>();
    private final Map<Integer, VirtualFieldNode> fieldNodes;

    /**
     * Creates a new ControlPanel.
     */
    public ControlPanel() {
        this.fieldNodes = new HashMap<>();
    }

    /**
     * Adds a new virtual field node to the control panel.
     *
     * @param fieldNode virtual field node to add
     * @return the address assigned to the virtual field node, -1 on error
     */
    public int addVirtualFieldNode(VirtualFieldNode fieldNode) {
        int address = -1;

        if ((null != fieldNode) && !(fieldNodes.containsValue(fieldNode))) {
            address = generateNewFieldNodeAddress();
            fieldNodes.put(address, fieldNode);
        }

        return address;
    }

    private int generateNewFieldNodeAddress() {
        int currentCheck = 0;

        while (fieldNodes.containsKey(currentCheck)) {
            currentCheck++;
        }

        return currentCheck;
    }

    private int getFieldNodeAddress(VirtualFieldNode fieldNode) {
        int address = -1;

        Iterator<Map.Entry<Integer, VirtualFieldNode>> fieldNodeIterator = fieldNodes.entrySet().iterator();

        while (address == -1 && fieldNodeIterator.hasNext()) {
            Map.Entry<Integer, VirtualFieldNode> entry = fieldNodeIterator.next();

            if (fieldNode.equals(entry.getValue())) {
                address = entry.getKey();
            }
        }

        return address;
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
     * Returns the field node with a given address.
     *
     * @param address the address for the field node
     * @return the virtual field node with the given address, null if no such virtual field node exists
     */
    public VirtualFieldNode getVirtualFieldNode(int address) {
        return fieldNodes.get(address);
    }

    /**
     * Returns the compatibility list for the control panel.
     *
     * @return the compatibility list
     */
    public Set<DeviceClass> getCompatibilityList() {
        return COMPATIBILITY_LIST;
    }

    @Override
    public void virtualActuatorStateChanged(VirtualFieldNode fieldNode, int actuatorAddress, boolean global) {
        int fieldNodeAddress = getFieldNodeAddress(fieldNode);

        if (fieldNodeAddress != -1) {

        }
    }

    @Override
    public void newSDUData(VirtualSDUSensor virtualSDUSensor) {

    }
}
