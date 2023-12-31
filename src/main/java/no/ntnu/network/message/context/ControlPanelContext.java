package no.ntnu.network.message.context;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.exception.NoSuchVirtualDeviceException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.sensordataprocess.UdpSensorDataSink;
import no.ntnu.tools.logger.SimpleLogger;
import no.ntnu.network.ControlCommAgent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A message context for processing control panel messages.
 */
public class ControlPanelContext extends ClientContext {
    private final ControlPanel controlPanel;
    private UdpSensorDataSink dataSink;

    /**
     * Creates a ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel
     * @param loggers the loggers
     */
    public ControlPanelContext(ControlCommAgent agent, ControlPanel controlPanel, Set<SimpleLogger> loggers) {
        super(agent, loggers);
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelContext, because control panel is null");
        }

        this.controlPanel = controlPanel;
    }

    /**
     * Creates a new ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel
     */
    public ControlPanelContext(ControlCommAgent agent, ControlPanel controlPanel) {
        this(agent, controlPanel, new HashSet<>());
    }

    /**
     * Sets the data sink for the control panel.
     *
     * @param dataSink the data sink
     */
    public void setDataSink(UdpSensorDataSink dataSink) {
        if (dataSink == null) {
            throw new IllegalArgumentException("Cannot set data sink, because dataSink is null.");
        }
        this.dataSink = dataSink;
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

    /**
     * Adds a field node pool to the control panel.
     *
     * @param fieldNodePool the field node pool to add
     */
    public void addFieldNodePool(Map<Integer, String> fieldNodePool) {
        if (fieldNodePool == null) {
            throw new IllegalArgumentException("Cannot add field node pool, because fieldNodePool is null.");
        }

        controlPanel.setFieldNodePool(fieldNodePool);
    }

    @Override
    public void register() {
        if (dataSink == null) {
            throw new IllegalStateException("Cannot register, because data sink has not yet been set.");
        }

        try {
            agent.sendRequest(new RegisterControlPanelRequest(controlPanel.getCompatibilityList(),
                    dataSink.getPortNumber()));
        } catch (IOException e) {
            logError("Cannot send registration request: " + e.getMessage());
        }
    }
}
