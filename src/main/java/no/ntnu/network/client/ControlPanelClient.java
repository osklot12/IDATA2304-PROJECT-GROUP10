package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.ControlPanelMessage;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.deserialize.MessageDeserializer;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.deserialize.NofspDeserializer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client<ControlPanelMessage> {
    private static final ByteSerializerVisitor SERIALIZER = NofspSerializer.getInstance();
    private static final MessageDeserializer<ControlPanelMessage> DESERIALIZER = new NofspControlPanelDeserializer();
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
        sendMessage(new RegisterControlPanelRequest(controlPanel.getCompatibilityList()));
    }

    @Override
    public void disconnect() {

    }

    @Override
    protected void processReceivedMessage(ControlPanelMessage message) {
        message.process(context);
    }
}
