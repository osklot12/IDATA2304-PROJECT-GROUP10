package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.sensordata.SensorDataMessage;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvFrame;

import java.io.IOException;

/**
 * A deserializer for deserializing control messages.
 *
 * @param <C> the message context used for the deserialized messages
 */
public interface MessageDeserializer<C extends MessageContext> {
    /**
     * Deserializes a control message TLV into a {@code Message} object.
     *
     * @param tlv the tlv to deserialize
     * @return the deserialized message
     * @throws IOException thrown if an I/O exception occurs
     */
    Message<C> deserializeMessage(Tlv tlv) throws IOException;

    /**
     * Returns the TLV frame used for deserialization.
     *
     * @return the tlv frame
     */
    TlvFrame getTlvFrame();
}
