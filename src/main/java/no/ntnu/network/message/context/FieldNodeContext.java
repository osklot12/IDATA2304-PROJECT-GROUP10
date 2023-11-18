package no.ntnu.network.message.context;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.ClientAgent;

/**
 * A context for processing field node messages.
 */
public class FieldNodeContext extends ClientContext {
    private final FieldNode fieldNode;

    /**
     * Creates a FieldNodeContext.
     *
     * @param agent the communication agent
     * @param fieldNode the field node to operate on
     */
    public FieldNodeContext(ClientAgent agent, FieldNode fieldNode) {
        super(agent);
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeContext, because field node is null");
        }

        this.fieldNode = fieldNode;
    }
}
