package no.ntnu.network.message.response;

import no.ntnu.network.message.common.ByteSerializableSecretKey;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.encryption.cipher.decrypt.AESDecryption;
import no.ntnu.network.message.encryption.cipher.encrypt.AESEncryption;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * A response to a {@code SymmetricEncryptionRequest}, indicating that symmetric key encryption has been initiated.
 *
 * @param <C> any client context
 */
public class SymmetricEncryptionResponse<C extends ClientContext> extends StandardProcessingResponseMessage<C> {
    private final SecretKey secretKey;

    /**
     * Creates a new SymmetricEncryptionResponse.
     *
     * @param secretKey the secret key
     */
    public SymmetricEncryptionResponse(SecretKey secretKey) {
        super(NofspSerializationConstants.SYMMETRIC_ENCRYPTION_CODE);
        if (secretKey == null) {
            throw new IllegalArgumentException("Cannot create SymmetricEncryptionResponse, because secretKey is null");
        }

        this.secretKey = secretKey;
    }

    /**
     * Creates a new SymmetricEncryptionResponse.
     *
     * @param id the message id
     * @param secretKey the secret key
     */
    public SymmetricEncryptionResponse(int id, SecretKey secretKey) {
        this(secretKey);

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(C context) {
        // sets the symmetric encryption strategy
        setSymmetricKeyEncryptionAndDecryption(context);

        // as communication is now secure, the client can register
        context.register();
    }

    /**
     * Sets a symmetric key strategy for both encryption and decryption.
     *
     * @param context the context to set for
     */
    private void setSymmetricKeyEncryptionAndDecryption(C context) {
        context.setEncryption(new AESEncryption(secretKey));
        context.setDecryption(new AESDecryption(secretKey));
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitResponseMessage(this, new ByteSerializableSecretKey(secretKey));
    }

    @Override
    public String toString() {
        return "symmetric key encryption has been successfully initiated";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SymmetricEncryptionResponse<?> s)) {
            return false;
        }

        return super.equals(s) && secretKey.equals(s.secretKey);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + secretKey.hashCode();

        return result;
    }
}
