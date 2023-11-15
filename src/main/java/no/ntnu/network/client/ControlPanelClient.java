package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;

import java.io.IOException;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client<ControlPanelContext> {
    private static final ByteSerializerVisitor SERIALIZER = NofspSerializer.getInstance();
    private static final MessageDeserializer<ControlPanelContext> DESERIALIZER = new NofspControlPanelDeserializer();
    private final ControlPanel controlPanel;
    private ControlPanelContext context;

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
        this.context = new ControlPanelContext(this, controlPanel);
    }

    @Override
    public void connect(String serverAddress) {
        if (connected()) {
            throw new IllegalStateException("Cannot connect control panel, because it is already connected.");
        }

        if (connectToServer(serverAddress, CentralServer.PORT_NUMBER, SERIALIZER, DESERIALIZER)) {
            registerControlPanel();
        }
    }

    /**
     * Registers the control panel at the server.
     */
    private void registerControlPanel() {
        try {
            sendRequest(new RegisterControlPanelRequest(controlPanel.getCompatibilityList()));
        } catch (IOException e) {
            Logger.error("Cannot send registration request: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    protected void processReceivedMessage(Message<ControlPanelContext> message) {
        try {
            message.process(context);
        } catch (IOException e) {
            Logger.error("Cannot process message: " + e.getMessage());
        }
    }
}
