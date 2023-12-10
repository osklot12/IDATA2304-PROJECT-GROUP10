package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.encryption.cipher.decrypt.RSADecryption;
import no.ntnu.network.message.encryption.keygen.AsymmetricKeyPairGenerator;
import no.ntnu.network.message.encryption.keygen.RSAKeyPairGenerator;
import no.ntnu.network.message.response.AsymmetricEncryptionResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.KeyGenError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * A request sent from any client to the central server, requesting the public key of the server to be used
 * for asymmetric encryption.
 */
public class AsymmetricEncryptionRequest extends RequestMessage implements Message<ServerContext> {
    /**
     * Creates a new AsymmetricEncryptionRequest.
     */
    public AsymmetricEncryptionRequest() {
        super(NofspSerializationConstants.ASYMMETRIC_ENCRYPTION_REQUEST);
    }

    /**
     * Creates a new AsymmetricEncryptionRequest.
     *
     * @param id the message id
     */
    public AsymmetricEncryptionRequest(int id) {
        this();

        setId(id);
    }

    @Override
    public void process(ServerContext context) throws IOException {
        // logs the receiving of the request
        context.logReceivingRequest(this);

        try {
            KeyPair pair = generateKeyPair();

            ResponseMessage response = new AsymmetricEncryptionResponse<>(pair.getPublic());
            sendResponse(context, response);

            // in the case of a successful request, the decryption strategy now needs to change
            context.setDecryption(new RSADecryption(pair.getPrivate()));
        } catch (NoSuchAlgorithmException e) {
            ResponseMessage response = new KeyGenError<>(e.getMessage());
            sendResponse(context, response);
        }
    }

    /**
     * Sends a response.
     *
     * @param context the context to send response with
     * @param response the response message to send
     * @throws IOException thrown if an I/O exception occurs
     */
    private void sendResponse(ServerContext context, ResponseMessage response) throws IOException {
        setResponseId(response);
        context.respond(response);
    }

    /**
     * Generates an asymmetric key pair.
     *
     * @return the asymmetric key pair
     * @throws NoSuchAlgorithmException thrown if the encryption algorithm cannot be found
     */
    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        AsymmetricKeyPairGenerator keyGen = new RSAKeyPairGenerator();
        keyGen.createKeys();

        return keyGen.getKeyPair();
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitRequestMessage(this);
    }

    @Override
    public String toString() {
        return "requesting to initiate asymmetric key encryption";
    }
}
