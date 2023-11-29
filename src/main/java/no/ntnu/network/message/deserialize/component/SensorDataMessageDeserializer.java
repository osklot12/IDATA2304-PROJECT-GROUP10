package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;

import java.io.IOException;

/**
 * A deserializer for deserializing sensor data messages.
 */
public interface SensorDataMessageDeserializer {
    /**
     * Deserializes a sensor data message TLV into a {@code SensorDataMessage} object.
     *
     * @param tlv the sensor data message tlv
     * @return the deserialized sensor data message
     * @throws IOException thrown if an I/O exception occurs
     */
    SensorDataMessage deserializeSensorData(Tlv tlv) throws IOException;
}
