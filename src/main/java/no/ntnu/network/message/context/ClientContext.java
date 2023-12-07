package no.ntnu.network.message.context;

import no.ntnu.tools.SimpleLogger;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.tools.eventformatter.ClientEventFormatter;

import java.util.Set;

/**
 * A message context for processing client messages.
 */
public abstract class ClientContext extends MessageContext {
    private final Set<SimpleLogger> loggers;

    /**
     * Creates a new ClientContext.
     *
     * @param agent the communication agent
     */
    protected ClientContext(ControlCommAgent agent, Set<SimpleLogger> loggers) {
        super(agent);
        if (loggers == null) {
            throw new IllegalArgumentException("Cannot create ClientContext, because loggers is null.");
        }

        this.loggers = loggers;
    }

    @Override
    public void logReceivingRequest(RequestMessage request) {
        loggers.forEach(logger -> logger.logInfo(ClientEventFormatter.requestReceived(request)));
    }

    @Override
    public void logReceivingResponse(ResponseMessage response) {
        loggers.forEach(logger -> logger.logInfo(ClientEventFormatter.responseReceived(response)));
    }

}
