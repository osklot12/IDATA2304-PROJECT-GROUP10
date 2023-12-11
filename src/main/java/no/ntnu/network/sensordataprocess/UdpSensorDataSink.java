package no.ntnu.network.sensordataprocess;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.message.deserialize.component.SensorDataMessageDeserializer;
import no.ntnu.network.message.encryption.TlvEncryption;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.decrypt.PlainTextDecryption;
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
    private static final int MAX_DATAGRAM_SIZE = 800; // careful of not exceeding the MTU
    private final UdpDatagramReceiver datagramReceiver;
    private final SensorDataMessageDeserializer deserializer;
    private DecryptionStrategy decryption;

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
        this.decryption = new PlainTextDecryption();
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
        this.decryption = new PlainTextDecryption();
    }

    /**
     * Sets the decryption used for receiving Tlvs.
     *
     * @param decryption the decryption strategy to use
     */
    public void setDecryption(DecryptionStrategy decryption) {
        if (decryption == null) {
            throw new IllegalArgumentException("Cannot set decryption, because decryption strategy is null.");
        }

        this.decryption = decryption;
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

        Tlv rawTlv = receiveNextRawTlv();
        Tlv processedTlv = processTlv(rawTlv);
        message = deserializer.deserializeSensorData(processedTlv);

        return message;
    }

    /**
     * Processes a raw TLV.
     *
     * @param rawTlv the raw tlv to process
     * @return the processed tlv
     * @throws IOException thrown if an I/O exception occurs
     */
    private Tlv processTlv(Tlv rawTlv) throws IOException {
        Tlv processedTlv = null;

        try {
            processedTlv = TlvEncryption.decryptTlv(rawTlv, decryption);
        } catch (EncryptionException e) {
            throw new IOException("Could not process the TLV: " + e.getMessage());
        }

        return processedTlv;
    }

    /**
     * Receives the next raw Tlv from the remote socket, without any processing.
     *
     * @return the next raw tlv
     * @throws IOException thrown if an I/O exception occurs
     */
    private Tlv receiveNextRawTlv() throws IOException {
        DatagramPacket receivedDatagram = datagramReceiver.getNextDatagramPacket();
        byte[] messageBytes = Arrays.copyOfRange(receivedDatagram.getData(), 0, receivedDatagram.getLength());

        return TlvReader.constructTlv(messageBytes, NofspSerializationConstants.TLV_FRAME);
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
