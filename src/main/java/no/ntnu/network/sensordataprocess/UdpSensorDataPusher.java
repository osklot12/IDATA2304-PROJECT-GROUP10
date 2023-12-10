package no.ntnu.network.sensordataprocess;

import no.ntnu.exception.EncryptionException;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.message.encryption.TlvEncryption;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.PlainTextEncryption;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
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
    private EncryptionStrategy encryption;

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
        this.encryption = new PlainTextEncryption();
    }

    /**
     * Sets the encryption used for sending Tlvs.
     *
     * @param encryption the encryption strategy to use
     */
    public void setEncryption(EncryptionStrategy encryption) {
        if (encryption == null) {
            throw new IllegalArgumentException("Cannot set encryption, because encryption strategy is null.");
        }

        this.encryption = encryption;
    }

    @Override
    public void sendSensorData(SensorDataMessage sensorData) throws IOException {
        Tlv rawTlv = serializer.serialize(sensorData);
        Tlv processedTlv = processTlv(rawTlv);
        messageSender.sendMessage(processedTlv.toBytes(), destIpAddress, destPortNumber);
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
            processedTlv = TlvEncryption.encryptTlv(rawTlv, encryption);
        } catch (EncryptionException e) {
            throw new IOException("Could not process the TLV: " + e.getMessage());
        }

        return processedTlv;
    }
}
