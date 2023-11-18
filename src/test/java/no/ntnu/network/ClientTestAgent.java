package no.ntnu.network;

/**
 * A client communication agent for testing purposes.
 * As some messages requires a client context for processing, and instance of this class provides just that, without
 * having to implement actual network communication logic.
 */
public class ClientTestAgent extends TestAgent implements ClientCommunicationAgent {
    private int nodeAddress = -1;

    @Override
    public void setClientNodeAddress(int address) {
        nodeAddress = address;
    }

    /**
     * Returns the client node address.
     *
     * @return client node address
     */
    public int getNodeAddress() {
        return nodeAddress;
    }
}
