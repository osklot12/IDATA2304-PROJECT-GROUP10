package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.HashSet;

/**
 * A serializable set of {@code ByteSerializable} objects.
 *
 * @param <T> any class implementing the {@code ByteSerializable} interface
 */
public class ByteSerializableSet<T extends ByteSerializable> extends HashSet<T> implements ByteSerializable {
    /**
     * Creates a new ByteSerializableSet.
     */
    public ByteSerializableSet() {
        super();
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitSet(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteSerializableSet<?>)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
