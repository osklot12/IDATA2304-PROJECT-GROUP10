package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
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
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
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
