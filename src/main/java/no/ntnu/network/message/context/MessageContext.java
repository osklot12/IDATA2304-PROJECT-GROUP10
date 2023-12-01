package no.ntnu.network.message.context;

import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A set of operations that are used by messages for processing, encapsulating the logic required.
 * The class acts as an intermediate layer between the messages and the objects on which they operate on.
 */
public abstract class MessageContext {
    protected final ControlCommAgent agent;

    /**
     * Creates a new MessageContext.
     *
     * @param agent the communication agent
     */
    protected MessageContext(ControlCommAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Cannot create MessageContext, because agent is null.");
        }

        this.agent = agent;
    }

    /**
     * Responds to the remote peer.
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
     * Asks for the response to be accepted, and returns the associated request message if so.
     *
     * @param response response message to accept
     * @return the associated request message, null if not accepted
     */
    public RequestMessage acceptResponse(ResponseMessage response) {
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

    /**
     * Closes the connection.
     */
    public void closeConnection() {
        agent.close();
    }
}
