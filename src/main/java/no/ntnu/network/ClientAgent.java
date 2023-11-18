package no.ntnu.network;

/**
 * A communication agent for the connection at the client-side.
 */
public interface ClientAgent extends CommunicationAgent {
    /**
     * Sets the node address for the client agent.
     *
     * @param address the client node address
     */
    void setClientNodeAddress(int address);
}
