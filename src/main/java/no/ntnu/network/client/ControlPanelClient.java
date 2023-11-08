package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.tool.TlvReader;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient {
    private final ControlPanel controlPanel;
    private OutputStream socketWriter;

    /**
     * Creates a new ControlPanelClient.
     *
     * @param controlPanel the control panel
     */
    public ControlPanelClient(ControlPanel controlPanel) {
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelClient, because control panel is null.");
        }

        this.controlPanel = controlPanel;
    }

    /**
     * Connects the client to a server.
     *
     * @param serverAddress ip address of a central server
     */
    public void connect(String serverAddress) {
        if (establishConnection(serverAddress)) {

        }
    }

    private boolean establishConnection(String serverAddress) {
        boolean connected = false;

        try {
            Socket socket = new Socket(serverAddress, CentralServer.PORT_NUMBER);
            socketWriter = socket.getOutputStream();
            connected = true;
        } catch (IOException e) {
            Logger.error("Cannot establish connection to server " + serverAddress + ": " + e.getMessage());
        }

        return connected;
    }
}
