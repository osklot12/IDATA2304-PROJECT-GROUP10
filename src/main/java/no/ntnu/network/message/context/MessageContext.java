package no.ntnu.network.message.context;

import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.message.encryption.cipher.decrypt.DecryptionStrategy;
import no.ntnu.network.message.encryption.cipher.encrypt.EncryptionStrategy;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.logger.SimpleLogger;

import java.io.IOException;
import java.util.Set;

/**
 * A set of operations that are used by messages for processing, encapsulating the logic required.
 * The class acts as an intermediate layer between the messages and the objects on which they operate on.
 */
public abstract class MessageContext {
    protected final ControlCommAgent agent;
    private final Set<SimpleLogger> loggers;

    /**
     * Creates a new MessageContext.
     *
     * @param agent the communication agent
     */
    protected MessageContext(ControlCommAgent agent, Set<SimpleLogger> loggers) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create MessageContext, because agent is null.");
        }

        if (loggers == null) {
            throw new IllegalArgumentException("Cannot create MessageContext, because loggers is null.");
        }

        this.agent = agent;
        this.loggers = loggers;
    }

    /**
     * Responds to the remote peer.
     *
     * @param response the response message
     * @throws IOException thrown if an I/O exception is thrown
     */
    public void respond(ResponseMessage response) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Cannot respond, because response is null.");
        }

        agent.sendResponse(response);
    }

    /**
     * Asks for the response to be accepted, and returns the associated request message if so.
     *
     * @param response response message to accept
     * @return the associated request message, null if not accepted
     */
    public RequestMessage acceptResponse(ResponseMessage response) {
        if (response == null) {
            throw new IllegalArgumentException("Cannot accept response, because response is null.");
        }

        return agent.acceptResponse(response);
    }

    /**
     * Sets the client node address.
     *
     * @param nodeAddress the client node address
     */
    public void setClientNodeAddress(int nodeAddress) {
        agent.setClientNodeAddress(nodeAddress);
    }

    /**
     * Logs the receiving of a request message.
     *
     * @param request the received request
     */
    public abstract void logReceivingRequest(RequestMessage request);

    /**
     * Logs the receiving of a response message.
     *
     * @param response the received response
     */
    public abstract void logReceivingResponse(ResponseMessage response);

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        agent.close();
    }

    /**
     * Sets the encryption strategy used to send messages.
     *
     * @param encryption the encryption strategy to use
     */
    public void setEncryption(EncryptionStrategy encryption) {
        agent.setEncryption(encryption);
    }

    /**
     * Returns whether received messages are secure or not.
     *
     * @return true if received messages are secure, false otherwise
     */
    public boolean receivedMessagesSecure() {
        return agent.receivedMessagesSecure();
    }

    /**
     * Sets the decryption strategy used to receive messages.
     *
     * @param decryption the decryption strategy to use
     */
    public void setDecryption(DecryptionStrategy decryption) {
        agent.setDecryption(decryption);
    }

    /**
     * Logs info.
     *
     * @param message the information to log
     */
    protected void logInfo(String message) {
        loggers.forEach(logger -> logger.logInfo(message));
    }

    /**
     * Logs an error.
     *
     * @param error the error message to log
     */
    protected void logError(String error) {
        loggers.forEach(logger -> logger.logError(error));
    }
}
