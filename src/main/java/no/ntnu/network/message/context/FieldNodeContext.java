package no.ntnu.network.message.context;

import no.ntnu.fieldnode.FieldNode;
import no.ntnu.network.ClientCommunicationAgent;

/**
 * A context for processing messages for a field node.
 * The context provides access to objects that control messages needs for processing.
 */
public class FieldNodeContext extends ClientContext {
    private final FieldNode fieldNode;

    /**
     * Creates a FieldNodeContext.
     *
     * @param agent the communication agent
     * @param fieldNode the field node to operate on
     */
    public FieldNodeContext(ClientCommunicationAgent agent, FieldNode fieldNode) {
        super(agent);
        if (fieldNode == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeContext, because field node is null");
        }

        this.fieldNode = fieldNode;
    }
}
