package no.ntnu.network.sensordataprocess;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Receives UDP datagram packets from another network entity.
 */
public class UdpDatagramReceiver {
    private final DatagramSocket datagramSocket;
    private final int maxDatagramBytes;

    /**
     * Creates a new UdpDatagramReceiver.
     *
     * @param datagramSocket the datagram socket to receive datagrams from
     * @param maxDatagramBytes the max amount of bytes allowed for each received datagram
     */
    public UdpDatagramReceiver(DatagramSocket datagramSocket, int maxDatagramBytes) {
        if (datagramSocket == null) {
            throw new IllegalArgumentException("Cannot create UdpDatagramReceiver, because datagramSocket is null.");
        }

        if (maxDatagramBytes < 9) {
            throw new IllegalArgumentException("Cannot create UdpDatagramReceiver, because max datagram bytes allowed" +
                    "is smaller than 9 bytes.");
        }

        this.datagramSocket = datagramSocket;
        this.maxDatagramBytes = maxDatagramBytes;
    }

    /**
     * Returns the next received datagram packet.
     *
     * @return next received datagram packet
     * @throws IOException thrown if an I/O exception occurs
     */
    public DatagramPacket getNextDatagramPacket() throws IOException {
        DatagramPacket datagramPacket = null;

        byte[] dataBuffer = new byte[maxDatagramBytes];
        datagramPacket = new DatagramPacket(dataBuffer, dataBuffer.length);
        datagramSocket.receive(datagramPacket);

        return datagramPacket;
    }

    /**
     * Returns the port number for the datagram receiver.
     *
     * @return the port number
     */
    public int getDatagramSocketPortNumber() {
        return datagramSocket.getLocalPort();
    }
}
