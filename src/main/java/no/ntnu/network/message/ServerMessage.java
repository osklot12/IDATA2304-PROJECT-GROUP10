package no.ntnu.network.message;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.context.ServerContext;

/**
 * A message processed by the central server.
 */
public interface ServerMessage extends Message<ServerContext> {
    /**
     * Processes the message.
     *
     * @param context the message context
     */
    void process(ServerContext context);
}
