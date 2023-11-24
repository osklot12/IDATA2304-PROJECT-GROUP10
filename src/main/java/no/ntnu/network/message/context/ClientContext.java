package no.ntnu.network.message.context;

import no.ntnu.network.CommunicationAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.ClientLogger;

import java.io.IOException;

/**
 * A context for processing client messages.
 */
public abstract class ClientContext extends MessageContext {
    /**
     * Creates a new ClientContext.
     *
     * @param agent the communication agent
     */
    protected ClientContext(CommunicationAgent agent) {
        super(agent);
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
