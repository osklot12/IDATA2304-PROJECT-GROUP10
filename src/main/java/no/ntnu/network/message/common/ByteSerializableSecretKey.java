package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * A serializable secret key.
 */
public record ByteSerializableSecretKey(SecretKey key) implements ByteSerializable {
    /**
     * Creates a new ByteSerializableSecretKey.
     *
     * @param key the secret key
     */
    public ByteSerializableSecretKey {
        if (key == null) {
            throw new IllegalArgumentException("Cannot create ByteSerializableSecretKey, because key is null.");
        }

    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitSecretKey(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ByteSerializableSecretKey b)) {
            return false;
        }

        return key.equals(b.key());
    }

}
