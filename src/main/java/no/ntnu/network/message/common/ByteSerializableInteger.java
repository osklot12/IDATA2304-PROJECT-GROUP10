package no.ntnu.network.message.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A serializable integer.
 */
public class ByteSerializableInteger implements ByteSerializable {
    private final int integer;

    /**
     * Creates a new ByteSerializableInteger.
     *
     * @param i any integer
     */
    public ByteSerializableInteger(int i) {
        this.integer = i;
    }

    /**
     * Returns the integer.
     *
     * @return the integer
     */
    public int getInteger() {
        return integer;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitInteger(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteSerializableInteger b)) {
            return false;
        }

        return b.getInteger() == integer;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + integer;

        return result;
    }

    @Override
    public String toString() {
        return Integer.toString(integer);
    }
}
