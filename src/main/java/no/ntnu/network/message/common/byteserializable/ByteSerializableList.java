package no.ntnu.network.message.common.byteserializable;

import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * A serializable list of {@code ByteSerializable} objects.
 *
 * @param <T> any class implementing the {@code ByteSerializable} interface
 */
public class ByteSerializableList<T extends ByteSerializable> implements ByteSerializable {
    private final List<T> list;

    /**
     * Creates a new ByteSerializableList.
     */
    public ByteSerializableList() {
        this.list = new ArrayList<>();
    }

    /**
     * Returns the list.
     *
     * @return list of {@code ByteSerializable} objects
     */
    public List<T> getList() {
        return this.list;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) {
        return visitor.visitList(this);
    }
}
