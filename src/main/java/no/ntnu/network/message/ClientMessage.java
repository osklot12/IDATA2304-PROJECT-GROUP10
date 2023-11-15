package no.ntnu.network.message;

import no.ntnu.network.message.context.ClientContext;

/**
 * A message processed by a client.
 *
 * @param <C> any client message context used for message processing
 */
public interface ClientMessage<C extends ClientContext> extends Message<C> {
    void process(C context);
}
