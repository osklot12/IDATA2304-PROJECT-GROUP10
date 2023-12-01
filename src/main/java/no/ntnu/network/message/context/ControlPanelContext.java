package no.ntnu.network.message.context;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.controlpanel.virtual.VirtualSDUSensor;
import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.ControlCommAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * A message context for processing control panel messages.
 */
public class ControlPanelContext extends ClientContext {
    private final ControlPanel controlPanel;

    /**
     * Creates a ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel to operate on
     */
    public ControlPanelContext(ControlCommAgent agent, ControlPanel controlPanel) {
        super(agent);
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelContext, because control panel is null");
        }

        this.controlPanel = controlPanel;
    }

    /**
     * Adds a new virtual field node to the control panel.
     *
     * @param fieldNodeAddress the address of the field node
     * @param fnst the field node system table
     * @param fnsm the field node status map
     * @param name the name of the field node
     */
    public void addVirtualFieldNode(int fieldNodeAddress, Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
        VirtualFieldNode virtualFieldNode = new VirtualFieldNode(name);
        virtualFieldNode.addVirtualDevicesFromFnst(fnst);
        virtualFieldNode.setVirtualActuatorStatesFromFnsm(fnsm);
        controlPanel.addVirtualFieldNode(virtualFieldNode, fieldNodeAddress);
    }

    /**
     * Removes a virtual field node from the control panel.
     *
     * @param fieldNodeAddress the address of the field node to remove
     */
    public void removeVirtualFieldNode(int fieldNodeAddress) {
        controlPanel.removeVirtualFieldNode(fieldNodeAddress);
    }

    /**
     * Sets the state of a virtual field node actuator.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the new state for the actuator
     * @throws NoSuchVirtualDeviceException thrown if addresses are invalid
     */
    public void setActuatorStatus(int fieldNodeAddress, int actuatorAddress, int newState) throws NoSuchVirtualDeviceException {
        VirtualFieldNode virtualFieldNode = controlPanel.getVirtualFieldNode(fieldNodeAddress);
        if (virtualFieldNode != null) {
            virtualFieldNode.setVirtualStandardActuatorState(actuatorAddress, newState, false);
        } else {
            throw new NoSuchVirtualDeviceException("Cannot set state of actuator " + actuatorAddress + " for " +
                    "field node " + fieldNodeAddress + ", because no such virtual field node exists.");
        }
    }
}
