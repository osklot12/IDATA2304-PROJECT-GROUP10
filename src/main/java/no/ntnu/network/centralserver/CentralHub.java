package no.ntnu.network.centralserver;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;

/**
 * A class responsible for managing clients.
 */
public class CentralHub {
    private final ClientRegister clientRegister;

    /**
     * Creates a new CentralCore.
     */
    public CentralHub() {
        this.clientRegister = new ClientRegister();
    }

    /**
     * Registers a client to the hub.
     *
     * @param client client to register
     * @return the assigned address for the client, -1 on error
     * @throws ClientRegistrationException thrown if client already exists
     */
    public int registerClient(ClientProxy client) throws ClientRegistrationException {
        int clientAddress = clientRegister.addClient(client);
        if (clientAddress == -1) {
            throw new ClientRegistrationException("Cannot register client, because client is already registered.");
        }

        return clientAddress;
    }
}
