package no.ntnu.network.sensordataprocess;

import no.ntnu.network.message.deserialize.component.SensorDataMessageDeserializer;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * A sensor data process responsible for receiving sensor data messages from a specific source using UDP.
 */
public class UdpSensorDataSink {
    private static final int MAX_DATAGRAM_SIZE = 1024; // careful of not exceeding the MTU
    private final UdpDatagramReceiver datagramReceiver;
    private final SensorDataMessageDeserializer deserializer;

    /**
     * Creates a new UdpSensorDataSink.
     * Automatically assigns a port number for the process.
     *
     * @param deserializer the deserializer used for message deserialization
     * @throws SocketException thrown if socket establishment fails
     */
    public UdpSensorDataSink(SensorDataMessageDeserializer deserializer) throws SocketException {
        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create UdpSensorDataSink, because deserializer is null.");
        }
        this.deserializer = deserializer;
        DatagramSocket datagramSocket = new DatagramSocket();
        this.datagramReceiver = new UdpDatagramReceiver(datagramSocket, MAX_DATAGRAM_SIZE);
    }

    /**
     * Creates a new UdpSensorDataSink.
     *
     * @param deserializer the deserializer used for message deserialization
     * @param portNumber the port number on which to receive datagrams
     * @throws SocketException thrown if socket establishment fails
     */
    public UdpSensorDataSink(SensorDataMessageDeserializer deserializer, int portNumber) throws SocketException {
        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create UdpSensorDataSink, because deserializer is null.");
        }
        this.deserializer = deserializer;
        DatagramSocket datagramSocket = new DatagramSocket(portNumber);
        this.datagramReceiver = new UdpDatagramReceiver(datagramSocket, MAX_DATAGRAM_SIZE);
    }

    /**
     * Returns the next received sensor data message.
     * This method blocks until a sensor data message is received.
     *
     * @return the next received sensor data message
     * @throws IOException thrown if an I/O exception occurs
     */
    public SensorDataMessage receiveNextMessage() throws IOException {
        SensorDataMessage message = null;

        DatagramPacket receivedDatagram = datagramReceiver.getNextDatagramPacket();
        byte[] messageBytes = Arrays.copyOfRange(receivedDatagram.getData(), 0, receivedDatagram.getLength());
        Tlv messageTlv = TlvReader.constructTlv(messageBytes, NofspSerializationConstants.TLV_FRAME);
        message = deserializer.deserializeSensorData(messageTlv);

        return message;
    }

    /**
     * Returns the local port number for the process.
     *
     * @return local port number
     */
    public int getPortNumber() {
        return datagramReceiver.getDatagramSocketPortNumber();
    }
}
