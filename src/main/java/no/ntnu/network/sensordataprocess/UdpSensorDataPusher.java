package no.ntnu.network.sensordataprocess;

import no.ntnu.network.DataCommAgent;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * A sensor data process responsible for pushing sensor data messages to a given destination using UDP.
 */
public class UdpSensorDataPusher implements DataCommAgent {
    private static final int MAX_DATAGRAM_SIZE = 1024; // careful of not exceeding the MTU
    private final InetAddress destIpAddress;
    private final int destPortNumber;
    private final UdpDatagramSender messageSender;
    private final ByteSerializerVisitor serializer;

    /**
     * Creates a new SensorDataProcess.
     *
     * @throws SocketException thrown if socket establishment fails
     */
    public UdpSensorDataPusher(InetAddress destIpAddress, int destPortNumber, ByteSerializerVisitor serializer) throws SocketException {
        this.destIpAddress = destIpAddress;
        this.destPortNumber = destPortNumber;
        DatagramSocket datagramSocket = new DatagramSocket();
        this.messageSender = new UdpDatagramSender(datagramSocket, serializer, MAX_DATAGRAM_SIZE);
        this.serializer = serializer;
    }

    @Override
    public void sendSensorData(SensorDataMessage sensorData) throws IOException {
        byte[] bytesToSend = serializer.serialize(sensorData);
        messageSender.sendMessage(bytesToSend, destIpAddress, destPortNumber);
    }
}
