package no.ntnu.network.message.common.byteserialized;

import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.serializerstrategy.SerializerStrategy;

public class ByteSerializedInteger implements ByteDeserializable {
    private final byte[] bytes;

    /**
     * Creates a ByteSerializedInteger.
     *
     * @param bytes integer serialized
     */
    public ByteSerializedInteger(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public ByteSerializable deserialize(SerializerStrategy strategy) {
        return strategy.deserialize(this);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
