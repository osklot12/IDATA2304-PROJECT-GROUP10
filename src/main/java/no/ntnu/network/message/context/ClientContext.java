package no.ntnu.network.message.context;

import no.ntnu.network.ClientCommunicationAgent;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A context for client messages to operate on.
 * The ClientContext provides common functionality shared by all clients.
 */
public abstract class ClientContext implements MessageContext {
    private final ClientCommunicationAgent agent;

    /**
     * Creates a ClientContext.
     *
     * @param agent the communication agent
     */
    protected ClientContext(ClientCommunicationAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create ClientContext, because communication agent is null");
        }

        this.agent = agent;
    }

    /**
     * Sets the client node address.
     *
     * @param address the node address
     */
    public void setNodeAddress(int address) {
        agent.setClientNodeAddress(address);
    }

    @Override
    public void respond(ResponseMessage response) throws IOException {
        agent.sendResponse(response);
    }

    @Override
    public boolean acceptResponse(ResponseMessage response) {
        return agent.acceptResponse(response);
    }
}
