package no.ntnu.network.message.context;

import no.ntnu.network.ClientAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.ClientLogger;

import java.io.IOException;

/**
 * A context for processing client messages.
 */
public abstract class ClientContext implements MessageContext {
    private final ClientAgent agent;

    /**
     * Creates a ClientContext.
     *
     * @param agent the communication agent
     */
    protected ClientContext(ClientAgent agent) {
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

    @Override
    public void logReceivingRequest(RequestMessage request) {
        ClientLogger.requestReceived(request);
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        ClientLogger.responseReceived(response);
    }
}
