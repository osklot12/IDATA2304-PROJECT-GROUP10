package no.ntnu.network.message.serialize;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

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
    byte[] accept(ByteSerializerVisitor visitor) throws SerializationException;
}
