package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableSecretKey;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.encryption.cipher.decrypt.AESDecryption;
import no.ntnu.network.message.encryption.cipher.encrypt.AESEncryption;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.SymmetricEncryptionResponse;
import no.ntnu.network.message.response.error.UnsecureRequestError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * A request sent from any client to the central server, requesting the server to initiate symmetric encryption
 * for further communication.
 */
public class SymmetricEncryptionRequest extends RequestMessage implements Message<ServerContext> {
    private final SecretKey secretKey;

    /**
     * Creates a new SymmetricEncryptionRequest.
     *
     * @param secretKey the secret key
     */
    public SymmetricEncryptionRequest(SecretKey secretKey) {
        super(NofspSerializationConstants.SYMMETRIC_ENCRYPTION_REQUEST);
        if (secretKey == null) {
            throw new IllegalArgumentException("Cannot create SymmetricEncryptionRequest, because secretKey is null.");
        }

        this.secretKey = secretKey;
    }

    /**
     * Creates a new SymmetricEncryptionRequest.
     *
     * @param id the message id
     * @param secretKey the secret key
     */
    public SymmetricEncryptionRequest(int id, SecretKey secretKey) {
        this(secretKey);

        setId(id);
    }

    @Override
    public void process(ServerContext context) throws IOException {
        // logs the receiving of the request
        context.logReceivingRequest(this);

        // only accept the secret key if it was sent encrypted
        if (context.receivedMessagesSecure()) {
            ResponseMessage response = new SymmetricEncryptionResponse<>(secretKey);
            sendResponse(context, response);
            initializeSymmetricEncryption(context);
        } else {
            ResponseMessage response = new UnsecureRequestError<>("Cannot initialize symmetric " +
                    "encryption, because the request was not secure, and may have been accessed by other parties.");
            sendResponse(context, response);
        }
    }

    /**
     * Sends a response message.
     *
     * @param context the context to respond for
     * @param response the response message
     * @throws IOException thrown if an I/O exception occurs
     */
    private void sendResponse(ServerContext context, ResponseMessage response) throws IOException {
        setResponseId(response);
        context.respond(response);
    }

    /**
     * Initializes symmetric key encryption.
     *
     * @param context the context to initialize for
     */
    private void initializeSymmetricEncryption(ServerContext context) {
        context.setEncryption(new AESEncryption(secretKey));
        context.setDecryption(new AESDecryption(secretKey));
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this, new ByteSerializableSecretKey(secretKey));
    }

    @Override
    public String toString() {
        return "requesting to initiate symmetric key encryption";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SymmetricEncryptionRequest s)) {
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
