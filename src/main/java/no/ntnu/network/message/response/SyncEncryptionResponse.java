package no.ntnu.network.message.response;

import no.ntnu.network.message.common.ByteSerializableSecretKey;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * A response sent to a {@code SyncEncryptionRequest}, indicating that the client is ready to start using
 * encrypted messages. The response contains the secret key which will be used for further communication.
 * This key is encrypted with the public key received by the server.
 */
public class SyncEncryptionResponse extends StandardProcessingResponseMessage<ServerContext> {
    private final SecretKey key;

    /**
     * Creates a new SyncEncryptionResponse.
     *
     * @param key the secret key to share
     */
    public SyncEncryptionResponse(SecretKey key) {
        super(NofspSerializationConstants.SYNC_ENCRYPTION_RESPONSE_CODE);
        if (key == null) {
            throw new IllegalArgumentException("Cannot create SecretKeySharingResponse, because key is null.");
        }

        this.key = key;
    }

    /**
     * Creates a new SyncEncryptionResponse.
     *
     * @param id the message id
     * @param key the secret key to share
     */
    public SyncEncryptionResponse(int id, SecretKey key) {
        this(key);

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ServerContext context) {

    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitResponseMessage(this, new ByteSerializableSecretKey(key));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SyncEncryptionResponse s)) {
            return false;
        }

        return super.equals(s) && key.equals(s.key);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + key.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "encryption synchronization was successful";
    }
}