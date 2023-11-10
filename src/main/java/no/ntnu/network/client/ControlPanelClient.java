package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.deserialize.NofspDeserializer;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.IOException;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client {
    private final ControlPanel controlPanel;

    /**
     * Creates a new ControlPanelClient.
     *
     * @param controlPanel the control panel
     */
    public ControlPanelClient(ControlPanel controlPanel) {
        super();
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelClient, because control panel is null.");
        }

        this.controlPanel = controlPanel;
    }

    @Override
    public void connect(String serverAddress) {
        if (connectToServer(serverAddress)) {

        }
    }

    private boolean connectToServer(String serverAddress) {
        return establishConnectionToServer(serverAddress, CentralServer.PORT_NUMBER,
                NofspSerializer.getInstance(), NofspDeserializer.getInstance());
    }

    @Override
    public void disconnect() {

    }
}
