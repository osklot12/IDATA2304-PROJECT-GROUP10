package no.ntnu.network.sensordataprocess;

import no.ntnu.network.DataCommAgent;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * A sensor data process responsible for pushing sensor data messages to a given destination.
 */
public class SensorDataPushingProcess implements DataCommAgent {
    private static final int MAX_DATAGRAM_SIZE = 1024;
    private final InetAddress destIpAddress;
    private final int destPortNumber;
    private final UdpMessageSender messageSender;

    /**
     * Creates a new SensorDataProcess.
     *
     * @throws SocketException thrown if socket establishment fails
     */
    public SensorDataPushingProcess(InetAddress destIpAddress, int destPortNumber, ByteSerializerVisitor serializer) throws SocketException {
        this.destIpAddress = destIpAddress;
        this.destPortNumber = destPortNumber;
        DatagramSocket datagramSocket = new DatagramSocket();
        messageSender = new UdpMessageSender(datagramSocket, serializer, MAX_DATAGRAM_SIZE);
    }

    @Override
    public void sendSensorData(SensorDataMessage sensorData) throws IOException {
        messageSender.sendMessage(sensorData, destIpAddress, destPortNumber);
    }
}
