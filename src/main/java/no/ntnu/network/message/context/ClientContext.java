package no.ntnu.network.message.context;

import no.ntnu.network.message.encryption.keygen.AESKeyGenerator;
import no.ntnu.network.message.encryption.keygen.SymmetricKeyGenerator;
import no.ntnu.network.message.request.SymmetricEncryptionRequest;
import no.ntnu.tools.logger.SimpleLogger;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.eventformatter.ClientEventFormatter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * A message context for processing client messages.
 */
public abstract class ClientContext extends MessageContext {
    /**
     * Creates a new ClientContext.
     *
     * @param agent the communication agent
     * @param loggers the loggers
     */
    protected ClientContext(ControlCommAgent agent, Set<SimpleLogger> loggers) {
        super(agent, loggers);
    }

    /**
     * Initializes the use of symmetric encryption for further communication.
     */
    public void initializeSymmetricEncryption() {
        SymmetricKeyGenerator keyGen = new AESKeyGenerator();
        if (createKey(keyGen)) {
            try {
                agent.sendRequest(new SymmetricEncryptionRequest(keyGen.getKey()));
            } catch (IOException e) {
                logError("Could not send request: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a secret key.
     *
     * @param keyGen the key generator
     * @return true if key was successfully created, false otherwise
     */
    private boolean createKey(SymmetricKeyGenerator keyGen) {
        boolean success = false;

        try {
            keyGen.createKey();
            success = true;
        } catch (NoSuchAlgorithmException e) {
            logError("Cannot create secret key: " + e.getMessage());
        }

        return success;
    }

    @Override
    public void logReceivingRequest(RequestMessage request) {
       logInfo(ClientEventFormatter.requestReceived(request));
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        logInfo(ClientEventFormatter.responseReceived(response));
    }

    /**
     * Registers the client at the central server.
     */
    public abstract void register();
}
