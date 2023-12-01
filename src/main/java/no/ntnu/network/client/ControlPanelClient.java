package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.request.*;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.sensordataprocess.UdpDatagramReceiver;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * A client for a control panel, connecting it to a central server using NOFSP.
 * The class is necessary for a control panel to be able to monitor and control field nodes in the network.
 */
public class ControlPanelClient extends Client<ControlPanelContext> {
    private final ControlPanel controlPanel;
    private final ControlPanelContext context;
    private final ByteSerializerVisitor serializer;
    private final NofspControlPanelDeserializer deserializer;
    private UdpDatagramReceiver datagramReceiver;
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
            try {
                Thread.sleep(10000);
                sendRequest(new DisconnectRequest());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean startHandlingIncomingSensorData() {
        boolean success = false;

        if (establishUdpMessageReceiver()) {
            Thread datagramReceivingThread = new Thread(() -> {
                while (running) {
                    handleNextReceivedDatagram();
                }
            });

            datagramReceivingThread.start();
            success = true;
        }

        return success;
    }

    private boolean establishUdpMessageReceiver() {
        boolean success = false;

        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramReceiver = new UdpDatagramReceiver(datagramSocket, 1024);
            success = true;
            Logger.info("Sensor data sink has been established on port " + datagramSocket.getLocalPort() + "...");
        } catch (IOException e) {
            Logger.error("Could not establish datagram socket: " + e.getMessage());
        }

        return success;
    }

    private void handleNextReceivedDatagram() {
        try {
            DatagramPacket receivedDatagram = datagramReceiver.getNextDatagramPacket();
            if (receivedDatagram != null) {
                ControlPanelDatagramHandler datagramHandler = new ControlPanelDatagramHandler(receivedDatagram, controlPanel, deserializer);
                datagramHandler.run();
            }
        } catch (IOException e) {
            Logger.error("Could not receive datagram packet: " + e.getMessage());
        }
    }

    /**
     * Registers the control panel at the server.
     */
    private void registerControlPanel() {
        try {
            sendRequest(new RegisterControlPanelRequest(controlPanel.getCompatibilityList(), datagramReceiver.getDatagramSocketPortNumber()));
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
}
