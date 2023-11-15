package no.ntnu.network;

/**
 * A communication agent for a client.
 */
public interface ClientCommunicationAgent extends CommunicationAgent {
    /**
     * Sets the node address for the client agent.
     *
     * @param address the client node address
     */
    void setClientNodeAddress(int address);
}
