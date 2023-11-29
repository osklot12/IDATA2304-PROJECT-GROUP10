package no.ntnu.network.client;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.controlpanel.virtual.VirtualFieldNode;
import no.ntnu.network.message.deserialize.NofspControlPanelDeserializer;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.tools.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Handles datagrams received by a control panel.
 */
public class ControlPanelDatagramHandler implements Runnable {
    private final DatagramPacket datagramPacket;
    private final ControlPanel controlPanel;
    private final NofspControlPanelDeserializer deserializer;

    /**
     * Creates a new ControlPanelDatagramHandler.
     *
     * @param datagramPacket the datagram packet to handle
     * @param controlPanel the control panel used for processing of the datagram
     * @param deserializer the deserializer used for sensor data message deserialization
     */
    public ControlPanelDatagramHandler(DatagramPacket datagramPacket, ControlPanel controlPanel, NofspControlPanelDeserializer deserializer) {
        if (datagramPacket == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelDatagramHandler, because datagramPacket is null.");
        }

        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelDatagramHandler, because controlPanel is null.");
        }

        if (deserializer == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelDatagramHandler, because deserializer is null.");
        }

        this.datagramPacket = datagramPacket;
        this.controlPanel = controlPanel;
        this.deserializer = deserializer;
    }


    @Override
    public void run() {
        SensorDataMessage message = deserializeMessage();
        if (message != null) {
            Logger.info("Sensor data received from sensor " + message.getSensorAddress());
            // extracts the contained data to the control panel
            message.extractData(controlPanel);
        }
    }

    /**
     * Deserializes the received sensor data message.
     *
     * @return the deserialized sensor data message
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
