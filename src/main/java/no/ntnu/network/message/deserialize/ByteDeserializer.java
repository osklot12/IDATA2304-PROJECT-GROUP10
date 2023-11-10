package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.tool.TlvFrame;

import java.io.IOException;

/**
 * A deserializer for deserializing bytes received over the network.
 */
public interface ByteDeserializer {
    /**
     * Deserializes an array of bytes.
     *
     * @param bytes bytes to deserialize
     * @return deserialized object
     * @throws IOException thrown if an I/O exception occurs
     */
    ByteSerializable deserialize(byte[] bytes) throws IOException;

    /**
     * Returns the TLV frame used for deserializing.
     *
     * @return tlv frame
     */
    TlvFrame getTlvFrame();
}
