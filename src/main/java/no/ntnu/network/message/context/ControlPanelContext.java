package no.ntnu.network.message.context;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.network.CommunicationAgent;

/**
 * A context for processing control panel messages.
 */
public class ControlPanelContext extends ClientContext {
    private final ControlPanel controlPanel;

    /**
     * Creates a ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel to operate on
     */
    public ControlPanelContext(CommunicationAgent agent, ControlPanel controlPanel) {
        super(agent);
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelContext, because control panel is null");
        }

        this.controlPanel = controlPanel;
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
