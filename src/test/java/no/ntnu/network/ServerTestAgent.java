package no.ntnu.network;

/**
 * A server communication agent for testing purposes.
 * As some messages requires a client context for processing, and instance of this class provides just that, without
 * having to implement actual network communication logic.
 */
public class ServerTestAgent extends TestAgent implements ServerAgent {
    private boolean clientRegistered = false;

    @Override
    public void registerClient() {
        clientRegistered = true;
    }

    /**
     * Returns whether the client is registered or not.
     *
     * @return true if registered
     */
    public boolean isClientRegistered() {
        return clientRegistered;
    }
}
