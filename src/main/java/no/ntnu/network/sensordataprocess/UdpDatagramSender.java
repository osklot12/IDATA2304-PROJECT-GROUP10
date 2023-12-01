package no.ntnu.network.sensordataprocess;

import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sends UDP datagram packets to another network entity.
 */
public class UdpDatagramSender {
    private final DatagramSocket datagramSocket;
    private final int maxDatagramSize;

    /**
     * Creates a new UDPMessageSender.
     *
     * @param datagramSocket the datagram socket to send messages to
     * @param maxDatagramSize the max size of datagrams allowed
     */
    public UdpDatagramSender(DatagramSocket datagramSocket, ByteSerializerVisitor serializer, int maxDatagramSize) {
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
        this.maxDatagramSize = maxDatagramSize;
    }

    /**
     * Sends a sensor data message to another network entity.
     *
     * @param bytes the bytes to send
     * @param destIpAddress the ip address of the destination
     * @param destPortNumber the port number of the destination
     * @throws IOException thrown if an I/O exception occurs
     */
    public void sendMessage(byte[] bytes, InetAddress destIpAddress, int destPortNumber) throws IOException {
        if (bytes.length > maxDatagramSize) {
            throw new IllegalArgumentException("Cannot send message, because its size is above the maximum allowed" +
                    "size of " + maxDatagramSize + " bytes.");
        }

        DatagramPacket packetToSend = new DatagramPacket(bytes, 0, bytes.length, destIpAddress, destPortNumber);
        datagramSocket.send(packetToSend);
    }
}
