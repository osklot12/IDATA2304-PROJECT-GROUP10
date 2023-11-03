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

    @Override
    public ByteDeserializable serialize(SerializerStrategy strategy) {
        return strategy.serializeInteger(integer);
    }
}
