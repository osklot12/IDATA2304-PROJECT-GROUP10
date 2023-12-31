package no.ntnu.network.client;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.FieldNodeListener;
import no.ntnu.network.centralserver.CentralServer;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.deserialize.NofspFieldNodeDeserializer;
import no.ntnu.network.message.deserialize.component.MessageDeserializer;
import no.ntnu.network.message.request.ActuatorNotificationRequest;
import no.ntnu.network.message.request.RegisterFieldNodeRequest;
import no.ntnu.network.message.sensordata.SduSensorDataMessage;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.network.representation.FieldNodeInformation;
import no.ntnu.network.sensordataprocess.UdpSensorDataPusher;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

/**
 * A client for a field node, connecting it to a central server using NOFSP.
 * The class is necessary for a field node to be able to push sensor data and share actuator control in the
 * network.
 */
public class FieldNodeClient extends Client<FieldNodeContext> implements FieldNodeListener {
    private final ByteSerializerVisitor serializer;
    private final MessageDeserializer<FieldNodeContext> deserializer;
    private final FieldNode fieldNode;
    private final String name;
    private final Set<Integer> adl;
    private final FieldNodeContext context;
    private UdpSensorDataPusher sensorDataProcess;

    /**
     * Creates a new FieldNodeClient.
     *
     * @param fieldNode the field node
     * @param name the name for the field node
     */
    public FieldNodeClient(FieldNode fieldNode, String name) {
        super();
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeClient, because field node is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeClient, because name is null.");
        }

        serializer = new NofspSerializer();
        deserializer = new NofspFieldNodeDeserializer();
        this.fieldNode = fieldNode;
        this.name = name;
        fieldNode.addListener(this);
        this.adl = new HashSet<>();
        this.context = new FieldNodeContext(this, fieldNode, this.adl, name, getLoggers());
    }

    @Override
    public void connect(String serverAddress) {
        if (isConnected()) {
            throw new IllegalStateException("Cannot connect field node, because it is already connected.");
        }

        if (connectToServer(serverAddress, CentralServer.CONTROL_PORT_NUMBER, serializer, deserializer)) {
            // connected and needs to register before using services of server
            initializeRegistration();
            try {
                establishSensorDataProcess();
            } catch (SocketException e) {
                logError("Could not establish sensor data process: " + e.getMessage());
            }
        }
    }

    private void establishSensorDataProcess() throws SocketException {
        sensorDataProcess = new UdpSensorDataPusher(getServerInetAddress(), CentralServer.DATA_PORT_NUMBER, serializer);
    }

    /**
     * Returns the name of the field node client.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sends a request to update the state of an actuator.
     *
     * @param actuatorAddress the address of the actuator
     * @param newState the new state of the actuator
     */
    private void requestActuatorStateUpdate(int actuatorAddress, int newState) {
        try {
            sendRequest(new ActuatorNotificationRequest(actuatorAddress, newState));
        } catch (IOException e) {
            logError("Cannot send request to update actuator state for actuator " + actuatorAddress);
        }
    }

    private void sendSensorDataMessage(SensorDataMessage message) {
        try {
            sensorDataProcess.sendSensorData(message);
        } catch (IOException e) {
            logError("Cannot send sensor data message: " + e.getMessage());
        }
    }

    @Override
    protected void processReceivedMessage(Message<FieldNodeContext> message) {
        try {
            message.process(context);
        } catch (IOException e) {
            logError("Cannot process received message: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void actuatorStateChange(int actuatorAddress, int newState) {
        // only request for an actuator state update if the actuator is listed in the ADL
        if (adl.contains(actuatorAddress)) {
            requestActuatorStateUpdate(actuatorAddress, newState);
        }
    }

    @Override
    public void sensorDataCapture(int sensorAddress, double data) {
        // only send the data if the sensor is listed in the ADL
        if (adl.contains(sensorAddress)) {
            sendSensorDataMessage(new SduSensorDataMessage(getClientNodeAddress(), sensorAddress, data));
        }
    }
}
