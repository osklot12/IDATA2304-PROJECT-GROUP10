package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.exception.NoServerConnectionException;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient {
    private final ControlPanel controlPanel;
    private final ByteSerializerVisitor serializer;
    private OutputStream outputStream;

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
        this.serializer = NofspSerializer.getInstance();
    }

    /**
     * Connects the client to a server.
     *
     * @param serverAddress ip address of a central server
     */
    public void connect(String serverAddress) {
        if (establishConnection(serverAddress)) {
            if (registerClient()) {

            }
        }
    }

    private boolean registerClient() {
        boolean success = false;

        RegisterControlPanelRequest registerRequest = new RegisterControlPanelRequest(controlPanel.getCompatibilityList());

        return success;
    }

    private boolean establishConnection(String serverAddress) {
        boolean connected = false;

        try {
            Socket socket = new Socket(serverAddress, CentralServer.PORT_NUMBER);
            outputStream = socket.getOutputStream();
            connected = true;
        } catch (IOException e) {
            Logger.error("Cannot establish connection to server " + serverAddress + ": " + e.getMessage());
        }

        return connected;
    }

    /**
     * Does a field node pool pull to the connected server.
     *
     * @throws NoServerConnectionException thrown if no connection to a server is found
     */
    public void getFieldNodePool() throws NoServerConnectionException {

    }
}
