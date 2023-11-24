package no.ntnu.network.message.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.util.ArrayList;

/**
 * A serializable list of {@code ByteSerializable} objects.
 *
 * @param <T> any class implementing the {@code ByteSerializable} interface
 */
public class ByteSerializableList<T extends ByteSerializable> extends ArrayList<T> implements ByteSerializable {
    /**
     * Creates a new ByteSerializableList.
     */
    public ByteSerializableList() {
        super();
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitList(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteSerializableList<?>)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
