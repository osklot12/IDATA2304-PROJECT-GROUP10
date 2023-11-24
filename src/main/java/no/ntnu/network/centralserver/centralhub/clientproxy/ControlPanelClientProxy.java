package no.ntnu.network.centralserver.centralhub.clientproxy;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.CommunicationAgent;

import java.util.Set;

/**
 * A proxy for a control panel client, used for storing information about the control panel and interacting with it.
 */
public class ControlPanelClientProxy extends ClientProxy {
    private final Set<DeviceClass> compatibilityList;

    /**
     * Creates a new ControlPanelClientProxy.
     *
     * @param agent the communication agent for the client
     */
    public ControlPanelClientProxy(CommunicationAgent agent, Set<DeviceClass> compatibilityList) {
        super(agent);

        this.compatibilityList = compatibilityList;
    }

    /**
     * Returns the compatibility list for the control panel client proxy.
     *
     * @return compatibility list
     */
    public Set<DeviceClass> getCompatibilityList() {
        return compatibilityList;
    }
}
