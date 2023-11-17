package no.ntnu.network.centralserver.clientproxy;

import no.ntnu.network.CommunicationAgent;

/**
 * A proxy for a remote client using the services of the server.
 * The ClientProxy stores useful data about the client, and provides means to communicate with it.
 * Instead of being dependent on a concrete communication technique, the client proxy relies on the
 * {@code CommunicationAgent} interface.
 */
public abstract class ClientProxy {
    private final CommunicationAgent agent;

    /**
     * Creates a new ClientProxy.
     *
     * @param agent the communication agent for the remote client
     */
    protected ClientProxy(CommunicationAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create ClientProxy, because communication agent is null.");
        }

        this.agent = agent;
    }

    /**
     * Returns the communication agent.
     *
     * @return the communication agent
     */
    public CommunicationAgent getAgent() {
        return agent;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ClientProxy c)) {
            return false;
        }

        return c.getAgent().equals(agent);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + agent.hashCode();

        return result;
    }
}
