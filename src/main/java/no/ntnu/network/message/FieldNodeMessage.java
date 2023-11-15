package no.ntnu.network.message;

import no.ntnu.network.message.context.FieldNodeContext;

/**
 * A message processed by a field node.
 */
public interface FieldNodeMessage extends ClientMessage<FieldNodeContext> {
    /**
     * Processes the message.
     *
     * @param context the message context
     */
    void process(FieldNodeContext context);
}
