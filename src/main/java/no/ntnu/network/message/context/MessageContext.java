package no.ntnu.network.message.context;

import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A context that can be used by messages for processing.
 * The MessageContext provides the message with necessary methods for processing, and encapsulates the means the logic
 * for this processing.
 */
public abstract class MessageContext {
    protected final CommunicationAgent agent;

    /**
     * Creates a new MessageContext.
     *
     * @param agent the communication agent
     */
    protected MessageContext(CommunicationAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create MessageContext, because agent is null.");
        }

        this.agent = agent;
    }

    /**
     * Responds to the remote peer.
     * Used when processing a request.
     *
     * @param response the response message
     * @throws IOException thrown if an I/O exception is thrown
     */
    public void respond(ResponseMessage response) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Cannot respond, because response is null.");
        }

        agent.sendResponse(response);
    }

    /**
     * Accepts a response message.
     * Used when processing a response.
     *
     * @return true if accepted, false otherwise
     */
    public boolean acceptResponse(ResponseMessage response) {
        if (response == null) {
            throw new IllegalArgumentException("Cannot accept response, because response is null.");
        }

        return agent.acceptResponse(response);
    }

    /**
     * Sets the client node address.
     *
     * @param nodeAddress the client node address
     */
    public void setClientNodeAddress(int nodeAddress) {
        agent.setClientNodeAddress(nodeAddress);
    }

    /**
     * Logs the receiving of a request message.
     *
     * @param request the received request
     */
    public abstract void logReceivingRequest(RequestMessage request);

    /**
     * Logs the receiving of a response message.
     *
     * @param response the received response
     */
    public abstract void logReceivingResponse(ResponseMessage response);
}
