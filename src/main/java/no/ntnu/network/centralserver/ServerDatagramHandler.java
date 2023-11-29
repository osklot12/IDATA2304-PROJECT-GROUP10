package no.ntnu.network.centralserver;

import no.ntnu.network.centralserver.centralhub.CentralHub;
import no.ntnu.network.message.deserialize.component.SensorDataMessageDeserializer;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.tools.logger.Logger;
import no.ntnu.tools.logger.ServerLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * A class handling received UDP datagrams.
 * As UDP per se is connectionless, a dedicated class is required for handling and routing the received datagrams
 * properly.
 */
public class ServerDatagramHandler implements Runnable {
    private final DatagramPacket datagramPacket;
    private final CentralHub centralHub;
    private final SensorDataMessageDeserializer deserializer;

    /**
     * Creates a new ServerDatagramHandler.
     *
     * @param datagramPacket the datagram packet to handle
     * @param centralHub the central hub used for processing of the datagram
     * @param deserializer the deserializer used for sensor data message deserialization
     */
    public ServerDatagramHandler(DatagramPacket datagramPacket, CentralHub centralHub, SensorDataMessageDeserializer deserializer) {
        if (datagramPacket == null) {
            throw new IllegalArgumentException("Cannot create ServerDatagramHandler, because datagramPacket is null.");
        }

        if (centralHub == null) {
            throw new IllegalArgumentException("Cannot create ServerDatagramHandler, because centralHub is null.");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create ServerDatagramHandler, because deserializer is null.");
        }

        this.datagramPacket = datagramPacket;
        this.centralHub = centralHub;
        this.deserializer = deserializer;
    }

    @Override
    public void run() {
        SensorDataMessage message = deserializeMessage();
        if (message != null) {
            centralHub.routeSensorDataToSubscribers(message);
        }
    }

    /**
     * Deserializes the received sensor data message.
     *
     * @return the received sensor data message
     */
    private SensorDataMessage deserializeMessage() {
        SensorDataMessage message = null;

        byte[] messageBytes = Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength());
        try {
            Tlv messageTlv = TlvReader.constructTlv(messageBytes, NofspSerializationConstants.TLV_FRAME);
            message = deserializer.deserializeSensorData(messageTlv);
        } catch (IOException e) {
            Logger.error("Cannot deserialize sensor data message: " + e.getMessage());
        }

        return message;
    }
}
