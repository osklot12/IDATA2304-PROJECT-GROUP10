package no.ntnu.network.message.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.util.HashMap;

/**
 * A serializable map.
 */
public class ByteSerializableMap<K extends ByteSerializable, V extends ByteSerializable> extends HashMap<K, V> implements ByteSerializable {
    /**
     * Creates a new ByteSerializableMap.
     */
    public ByteSerializableMap() {
        super();
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitMap(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteSerializableMap<?, ?> that = (ByteSerializableMap<?, ?>) o;
        return this.entrySet().equals(that.entrySet());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
