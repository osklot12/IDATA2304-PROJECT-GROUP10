package no.ntnu.network.centralserver;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;

/**
 * The CentralHub is the 'logic class' for the central server, responsible for managing clients.
 * Although the class does handle client communication, it is not dependent on a concrete communication implementation,
 * and can therefore handle client communication of any type.
 */
public class CentralHub {
    private final ClientRegister clientRegister;

    /**
     * Creates a new CentralHub.
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
