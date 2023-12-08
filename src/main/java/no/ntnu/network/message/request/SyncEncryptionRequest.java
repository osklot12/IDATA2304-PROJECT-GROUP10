package no.ntnu.network.message.request;

import no.ntnu.network.message.common.ByteSerializablePublicKey;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.security.PublicKey;

/**
 * A request sent from the server to any client, requesting to synchronize encryption in order to allow for
 * encryption of further communication. The request contains the public key of the server, which the client
 * can use to encrypt a secret key.
 */
public class SyncEncryptionRequest<C extends ClientContext> extends StandardProcessingRequestMessage<C> {
    private final PublicKey key;

    /**
     * Creates a new SyncEncryptionRequest.
     *
     * @param key the public key to share
     */
    public SyncEncryptionRequest(PublicKey key) {
        super(NofspSerializationConstants.SYNC_ENCRYPTION_COMMAND);
        if (key == null) {
            throw new IllegalArgumentException("Cannot create PublicKeySharingRequest, because key is null.");
        }

        this.key = key;
    }

    /**
     * Creates a new SyncEncryptionRequest.
     *
     * @param id the message id
     * @param key the public key to share
     */
    public SyncEncryptionRequest(int id, PublicKey key) {
        this(key);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(C context) {
        return null;
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this, new ByteSerializablePublicKey(key));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SyncEncryptionRequest<?> p)) {
            return false;
        }

        return super.equals(p) && key.equals(p.key);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + key.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "requesting to synchronize encryption";
    }
}
