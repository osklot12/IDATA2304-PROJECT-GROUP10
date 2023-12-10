package no.ntnu.network.message.response;

import no.ntnu.network.message.common.ByteSerializablePublicKey;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.encryption.cipher.encrypt.RSAEncryption;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.security.PublicKey;

/**
 * A response to a successful {@code AsymmetricEncryptionRequest}, providing the public key of the central server.
 *
 * @param <C> any client context
 */
public class AsymmetricEncryptionResponse<C extends ClientContext> extends StandardProcessingResponseMessage<C> {
    private final PublicKey publicKey;

    /**
     * Creates a new AsymmetricEncryptionResponse.
     *
     * @param publicKey the public key
     */
    public AsymmetricEncryptionResponse(PublicKey publicKey) {
        super(NofspSerializationConstants.ASYMMETRIC_ENCRYPTION_CODE);
        if (publicKey == null) {
            throw new IllegalArgumentException("Cannot create AsymmetricEncryptionResponse, because publicKey is null.");
        }

        this.publicKey = publicKey;
    }

    /**
     * Creates a new AsymmetricEncryptionResponse.
     *
     * @param id the message id
     * @param publicKey the public key
     */
    public AsymmetricEncryptionResponse(int id, PublicKey publicKey) {
        this(publicKey);

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(C context) {
        // logs the receiving of the response
        context.logReceivingResponse(this);

        // the encryption strategy now needs to change, to match the servers decryption strategy
        context.setEncryption(new RSAEncryption(publicKey));

        // initializes symmetric key encryption
        context.initializeSymmetricEncryption();
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitResponseMessage(this, new ByteSerializablePublicKey(publicKey));
    }

    @Override
    public String toString() {
        return "asymmetric key encryption has been successfully initiated";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AsymmetricEncryptionResponse<?> a)) {
            return false;
        }

        return super.equals(a) && publicKey.equals(a.publicKey);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + publicKey.hashCode();

        return result;
    }
}
