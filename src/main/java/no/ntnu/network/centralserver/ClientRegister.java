package no.ntnu.network.centralserver;

import no.ntnu.exception.ClientAlreadyRegisteredException;
import no.ntnu.exception.NoSuchClientException;
import no.ntnu.network.centralserver.clientproxy.ClientProxy;
import no.ntnu.network.centralserver.clientproxy.FieldNodeClientProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A register for storing information about the registered clients.
 * The ClientRegister is responsible for node addressing - assigning addresses for the clients.
 */
public class ClientRegister {
    private final Map<Integer, ClientProxy> register;

    /**
     * Creates a new ClientRegister.
     */
    public ClientRegister() {
        this.register = new HashMap<>();
    }

    /**
     * Adds a new client to the register.
     *
     * @param client client to add
     * @return the address for the client
     */
    public int addClient(ClientProxy client) {
        if (client == null) {
            throw new IllegalArgumentException("Cannot add client to ClientRegister, because client is null.");
        }

        int clientAddress = -1;

        if (!register.containsValue(client)) {
            clientAddress = generateNewClientAddress();
            register.put(clientAddress, client);
        }

        return clientAddress;
    }

    private int generateNewClientAddress() {
        int currentCheck = 0;

        while (register.containsKey(currentCheck)) {
            currentCheck++;
        }

        return currentCheck;
    }

    /**
     * Returns a client proxy with the given address.
     *
     * @param address address of client proxy
     * @return client proxy
     */
    public ClientProxy getClientProxy(int address) throws NoSuchClientException {
        if (!(register.containsKey(address))) {
            throw new NoSuchClientException("Cannot access client, because no client with address " + address + " exists.");
        }

        return register.get(address);
    }

    /**
     * Returns the field node pool, containing all field node proxies for the register.
     *
     * @return field node pool
     */
    public Map<Integer, FieldNodeClientProxy> getFieldNodePool() {
        Map<Integer, FieldNodeClientProxy> result = new HashMap<>();

        register.forEach((key, value) -> {
            if (value instanceof FieldNodeClientProxy clientProxy) {
                result.put(key, clientProxy);
            }
        });

        return result;
    }
}
