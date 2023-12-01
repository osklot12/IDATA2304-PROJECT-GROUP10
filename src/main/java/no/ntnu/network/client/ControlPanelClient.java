package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.connectionservice.sensordatarouter.SensorDataDestination;
import no.ntnu.network.connectionservice.sensordatarouter.UdpSensorDataRouter;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.sensordataprocess.UdpSensorDataSink;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.net.SocketException;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client<ControlPanelContext> implements SensorDataDestination {
    private final ControlPanel controlPanel;
    private final ControlPanelContext context;
    private final ByteSerializerVisitor serializer;
    private final NofspControlPanelDeserializer deserializer;
    private UdpSensorDataRouter sensorDataRouter;
    private volatile boolean running;

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
        this.deserializer = new NofspControlPanelDeserializer(controlPanel);
        this.context = new ControlPanelContext(this, controlPanel);
    }

    @Override
    public void connect(String serverAddress) {
        if (isConnected()) {
            throw new IllegalStateException("Cannot connect control panel, because it is already connected.");
        }

        running = true;
        if (startHandlingIncomingSensorData() && (connectToServer(serverAddress, CentralServer.CONTROL_PORT_NUMBER, serializer, deserializer))) {
            registerControlPanel();
            subscribeToFieldNode(0);

        }
    }

    /**
     * Starts handling incoming UDP sensor data.
     *
     * @return true if successfully listening for sensor data
     */
    private boolean startHandlingIncomingSensorData() {
        boolean success = false;

        try {
            UdpSensorDataSink sensorDataSink = new UdpSensorDataSink(deserializer);
            sensorDataRouter = new UdpSensorDataRouter(sensorDataSink);
            sensorDataRouter.addDestination(this);
            sensorDataRouter.start();
            success = true;
        } catch (SocketException e) {
            Logger.error("Could not start handling incoming sensor data: " + e.getMessage());
        }

        return success;
    }

    /**
     * Registers the control panel at the server.
     */
    private void registerControlPanel() {
        try {
            sendRequest(new RegisterControlPanelRequest(controlPanel.getCompatibilityList(), sensorDataRouter.getLocalPortNumber()));
        } catch (IOException e) {
            Logger.error("Cannot send registration request: " + e.getMessage());
        }
    }

    /**
     * Requests the field node pool.
     */
    public void requestFieldNodePool() {
        try {
            sendRequest(new FieldNodePoolPullRequest());
        } catch (IOException e) {
            Logger.error("Cannot send field node pool pull request: " + e.getMessage());
        }
    }

    /**
     * Requests to subscribe to a given field node.
     *
     * @param fieldNodeAddress the node address of the field node to subscribe to
     */
    public void subscribeToFieldNode(int fieldNodeAddress) {
        try {
            sendRequest(new SubscribeToFieldNodeRequest(fieldNodeAddress));
        } catch (IOException e) {
            Logger.error("Cannot send request to subscribe to field node with address " + fieldNodeAddress +
                    ": " + e.getMessage());
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
            Logger.error("Cannot process received message: " + e.getMessage());
        }
    }

    @Override
    public void receiveSensorData(SensorDataMessage sensorData) {
        Logger.info(sensorData.toString());
        sensorData.extractData(controlPanel);
    }
}
