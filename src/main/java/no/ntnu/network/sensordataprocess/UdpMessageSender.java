package no.ntnu.network.sensordataprocess;

import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sends UDP (sensor data) messages to another entity in the network.
 */
public class UdpMessageSender {
    private final DatagramSocket datagramSocket;
    private final ByteSerializerVisitor serializer;
    private final int maxDatagramSize;

    /**
     * Creates a new UDPMessageSender.
     *
     * @param datagramSocket the datagram socket to send messages to
     * @param serializer the serializer for message serialization
     * @param maxDatagramSize the max size of datagrams allowed
     */
    public UdpMessageSender(DatagramSocket datagramSocket, ByteSerializerVisitor serializer, int maxDatagramSize) {
        if (datagramSocket == null) {
            throw new IllegalArgumentException("Cannot create UDPMessageSender, because datagramSocket is null.");
        }

        if (serializer == null) {
            throw new IllegalArgumentException("Cannot create UDPMessageSender, because serializer is null.");
        }

        if (maxDatagramSize <= 8) {
            throw new IllegalArgumentException("Max datagram size must be bigger than 100 bytes.");
        }

        this.datagramSocket = datagramSocket;
        this.serializer = serializer;
        this.maxDatagramSize = maxDatagramSize;
    }

    /**
     * Sends a sensor data message to another network entity.
     *
     * @param message the sensor data message to send
     * @param destIpAddress the ip address of the destination
     * @param destPortNumber the port number of the destination
     * @throws IOException thrown if an I/O exception occurs
     */
    public void sendMessage(SensorDataMessage message, InetAddress destIpAddress, int destPortNumber) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("Cannot send message, because message is null.");
        }

        byte[] bytes = serializer.serialize(message);
        if (bytes.length > maxDatagramSize) {
            throw new IllegalArgumentException("Cannot send message, because its size is above the maximum allowed" +
                    "size of " + maxDatagramSize + " bytes.");
        }
        DatagramPacket packetToSend = new DatagramPacket(bytes, 0, bytes.length, destIpAddress, destPortNumber);
        datagramSocket.send(packetToSend);
    }
}
