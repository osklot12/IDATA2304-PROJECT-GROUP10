package no.ntnu.network.message.context;

import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.logger.ClientLogger;

/**
 * A message context for processing client messages.
 */
public abstract class ClientContext extends MessageContext {
    /**
     * Creates a new ClientContext.
     *
     * @param agent the communication agent
     */
    protected ClientContext(ControlCommAgent agent) {
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
