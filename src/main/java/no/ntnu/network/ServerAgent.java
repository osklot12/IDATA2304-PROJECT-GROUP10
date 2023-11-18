package no.ntnu.network;

/**
 * A communication agent for the connection at the server-side.
 */
public interface ServerAgent extends CommunicationAgent {
    /**
     * Registers the client, allowing for further communication.
     */
    void registerClient();
}
