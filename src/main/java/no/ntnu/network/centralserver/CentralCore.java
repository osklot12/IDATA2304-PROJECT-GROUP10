package no.ntnu.network.centralserver;

import no.ntnu.exception.ClientAlreadyRegisteredException;
import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;

/**
 * A class responsible for handling the client register and message routing.
 */
public class CentralCore {
    private final ClientRegister clientRegister;

    /**
     * Creates a new CentralCore.
     */
    public CentralCore() {
        this.clientRegister = new ClientRegister();
    }

    public void registerClient(ClientProxy client) throws ClientRegistrationException {
        try {
            clientRegister.addClient(client);
        } catch (ClientAlreadyRegisteredException e) {
            throw new ClientRegistrationException("Cannot register client: " + e.getMessage());
        }
    }
}
