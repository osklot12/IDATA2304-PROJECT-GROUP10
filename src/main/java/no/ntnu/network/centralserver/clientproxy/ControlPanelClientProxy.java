package no.ntnu.network.centralserver.clientproxy;

import no.ntnu.fieldnode.device.DeviceClass;

import java.net.Socket;
import java.util.Set;

/**
 * A proxy for a control panel client, used for storing information about the control panel and interacting with it.
 */
public class ControlPanelClientProxy extends ClientProxy {
    private final Set<DeviceClass> compatibilityList;

    /**
     * Creates a new ControlPanelClientProxy.
     *
     * @param clientSocket the client socket
     */
    public ControlPanelClientProxy(Socket clientSocket, Set<DeviceClass> compatibilityList) {
        super(clientSocket);

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
