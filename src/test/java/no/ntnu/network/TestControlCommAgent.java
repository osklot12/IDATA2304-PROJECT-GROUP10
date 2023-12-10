package no.ntnu.network;

import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.sensordataprocess.UdpDataCommAgentProvider;

import java.io.IOException;

/**
 * A communication agent for testing purposes.
 * It can be difficult and sometimes impossible to test real communication agents, and this class provides means
 * to test without having a proper agent.
 */
public class TestControlCommAgent implements ControlCommAgent, UdpDataCommAgentProvider {
    private final DataCommAgent dataCommAgent;
    private RequestMessage requestSent;
    private ResponseMessage responseSent;
    private int clientNodeAddress;
    private boolean closed;
    private EncryptionStrategy encryption;
    private DecryptionStrategy decryption;

    /**
     * Creates a new TestControlCommAgent.
     */
    public TestControlCommAgent() {
        this.dataCommAgent = new TestDataCommAgent();
        this.requestSent = null;
        this.responseSent = null;
        this.clientNodeAddress = -1;
        this.closed = false;
    }

    @Override
    public void sendRequest(RequestMessage request) throws IOException {
        requestSent = request;
    }

    @Override
    public void sendResponse(ResponseMessage response) throws IOException {
        responseSent = response;
    }

    @Override
    public RequestMessage acceptResponse(ResponseMessage responseMessage) {
        return requestSent;
    }

    @Override
    public String getRemoteEntityAsString() {
        return "Testentity";
    }

    @Override
    public int getClientNodeAddress() {
        return clientNodeAddress;
    }

    @Override
    public void setClientNodeAddress(int address) {
        clientNodeAddress = address;
    }

    @Override
    public void setEncryption(EncryptionStrategy encryption) {
        this.encryption = encryption;
    }

    @Override
    public boolean receivedMessagesSecure() {
        return false;
    }

    @Override
    public void setDecryption(DecryptionStrategy decryption) {
        this.decryption = decryption;
    }

    @Override
    public void close() {
        closed = true;
    }

    /**
     * Returns the last request sent.
     *
     * @return last request sent
     */
    public RequestMessage getRequestSent() {
        return requestSent;
    }

    /**
     * Returns the last response sent.
     *
     * @return last response sent
     */
    public ResponseMessage getResponseSent() {
        return responseSent;
    }

    /**
     * Returns whether the agent is closed or not.
     *
     * @return true if closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns the encryption strategy.
     *
     * @return the encryption strategy
     */
    public EncryptionStrategy getEncryption() {
        return encryption;
    }

    /**
     * Returns the decryption strategy.
     *
     * @return the decryption strategy
     */
    public DecryptionStrategy getDecryption() {
        return decryption;
    }

    @Override
    public DataCommAgent getDataCommAgent(int portNumber) throws IOException {
        return dataCommAgent;
    }
}
