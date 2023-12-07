package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.representation.FieldNodeAgent;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.connectionservice.sensordatarouter.UdpSensorDataRouter;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.sensordataprocess.UdpSensorDataSink;

import java.io.IOException;
import java.net.SocketException;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client<ControlPanelContext> implements FieldNodeAgent {
    private final ControlPanel controlPanel;
    private final ControlPanelContext context;
    private final ByteSerializerVisitor serializer;
    private final NofspControlPanelDeserializer deserializer;
    private UdpSensorDataRouter sensorDataRouter;

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

        this.serializer = new NofspSerializer();
        this.controlPanel = controlPanel;
        controlPanel.setFieldNodeAgent(this);
        this.deserializer = new NofspControlPanelDeserializer(controlPanel);
        this.context = new ControlPanelContext(this, controlPanel, getLoggers());
    }

    @Override
    public void connect(String serverAddress) throws IOException {
        if (isConnected()) {
            throw new IllegalStateException("Cannot connect control panel, because it is already connected.");
        }

        if (startHandlingIncomingSensorData()) {
            if (connectToServer(serverAddress, CentralServer.CONTROL_PORT_NUMBER, serializer, deserializer)) {
                registerControlPanel();
            } else {
                sensorDataRouter.stop();
                throw new IOException("Failed to connect to server with address: " + serverAddress);
            }
        } else {
            throw new IOException("Failed to establish sensor data process.");
        }
    }

    /**
     * Starts handling incoming UDP sensor data.
     *
     * @return true if successfully listening for sensor data
     */
    private boolean startHandlingIncomingSensorData() throws IOException {
        boolean success = false;

        try {
            UdpSensorDataSink sensorDataSink = new UdpSensorDataSink(deserializer);
            sensorDataRouter = new UdpSensorDataRouter(sensorDataSink);
            sensorDataRouter.addDestination(controlPanel);
            sensorDataRouter.start();
            success = true;
        } catch (SocketException e) {
            logError("Could not start handling incoming sensor data: " + e.getMessage());
        }

        return success;
    }

    /**
     * Registers the control panel at the server.
     */
    private void registerControlPanel() {
        try {
            sendRequest(new RegisterControlPanelRequest(controlPanel.getCompatibilityList(),
                    sensorDataRouter.getLocalPortNumber()));
        } catch (IOException e) {
            logError("Cannot send registration request: " + e.getMessage());
        }
    }

    /**
     * Returns the control panel.
     *
     * @return the control panel
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    @Override
    public void requestFieldNodePool() {
        try {
            sendRequest(new FieldNodePoolPullRequest());
        } catch (IOException e) {
            logError("Cannot send field node pool pull request: " + e.getMessage());
        }
    }

    @Override
    public void subscribeToFieldNode(int fieldNodeAddress) {
        try {
            sendRequest(new SubscribeToFieldNodeRequest(fieldNodeAddress));
        } catch (IOException e) {
            logError("Cannot send request to subscribe to field node with address " + fieldNodeAddress +
                    ": " + e.getMessage());
        }
    }

    @Override
    public void unsubscribeFromFieldNode(int fieldNodeAddress) {
        try {
            sendRequest(new UnsubscribeFromFieldNodeRequest(fieldNodeAddress));
        } catch (IOException e) {
            logError("Cannot send request to unsubscribe to field node with address " + fieldNodeAddress +
                    ": " + e.getMessage());
        }
    }

    @Override
    public void setActuatorState(int fieldNodeAddress, int actuatorAddress, int newState) {
        try {
            sendRequest(new ServerActivateActuatorRequest(fieldNodeAddress, actuatorAddress, newState));
        } catch (IOException e) {
            logError("Cannot send request to activate actuator with address " + actuatorAddress + " on field " +
                    "node " + fieldNodeAddress + ": " + e.getMessage());
        }
    }

    @Override
    public String getFieldNodeSourceAsString() {
        return getRemoteEntityAsString();
    }

    @Override
    public void disconnect() {

    }

    @Override
    protected void processReceivedMessage(Message<ControlPanelContext> message) {
        try {
            message.process(context);
        } catch (IOException e) {
            logError("Cannot process received message: " + e.getMessage());
        }
    }
}
