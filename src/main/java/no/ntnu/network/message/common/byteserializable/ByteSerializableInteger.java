package no.ntnu.network.message.common.byteserializable;

import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A deserialized integer that can be serialized.
 */
public class ByteSerializableInteger implements ByteSerializable {
    private static final int SERIALIZATION_CODE = 0;
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
    public byte[] accept(ByteSerializerVisitor visitor) {
        return new byte[0];
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
}
