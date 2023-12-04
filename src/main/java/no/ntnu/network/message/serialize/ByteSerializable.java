package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;

/**
 * A serializable object acting as the component in the Composite design pattern.
 * An ByteSerializable object serializes itself and all of its ByteSerializable children, with the use of a
 * {@code ByteSerializerVisitor}.
 */
public interface ByteSerializable {
    /**
     * Accepts a visitor to visit the node.
     *
     * @param visitor the visitor to accept
     * @return the serialized bytes for the object
     */
    Tlv accept(ByteSerializerVisitor visitor) throws IOException;
}
