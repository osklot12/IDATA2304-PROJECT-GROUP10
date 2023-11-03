package no.ntnu.network.message.common.bytedeserialized;

import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.serializerstrategy.SerializerStrategy;

/**
 * A deserialized integer that can be serialized.
 */
public class ByteDeserializedInteger implements ByteSerializable {
    private final int integer;

    /**
     * Creates a new ByteDeserializedInteger.
     *
     * @param i any integer
     */
    public ByteDeserializedInteger(int i) {
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
    public ByteDeserializable serialize(SerializerStrategy strategy) {
        return strategy.serializeInteger(integer);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteDeserializedInteger b)) {
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
