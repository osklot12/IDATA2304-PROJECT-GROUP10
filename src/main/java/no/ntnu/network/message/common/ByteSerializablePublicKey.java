package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.security.PublicKey;

/**
 * A serializable public key.
 */
public record ByteSerializablePublicKey(PublicKey key) implements ByteSerializable {
    /**
     * Creates a new ByteSerializablePublicKey.
     *
     * @param key the public key
     */
    public ByteSerializablePublicKey {
        if (key == null) {
            throw new IllegalArgumentException("Cannot create ByteSerializablePublicKey, because key is null.");
        }

    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitPublicKey(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteSerializablePublicKey b)) {
            return false;
        }

        return key.equals(b.key());
    }

}
