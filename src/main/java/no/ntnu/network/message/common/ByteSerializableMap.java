package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
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
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
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
