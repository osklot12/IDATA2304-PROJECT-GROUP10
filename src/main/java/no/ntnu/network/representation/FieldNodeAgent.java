package no.ntnu.network.representation;

/**
 * An agent interacting with field nodes, used to access information and manage field nodes.
 */
public interface FieldNodeAgent {
    /**
     * Requests a field node pool.
     */
    void requestFieldNodePool();

    /**
     * Subscribes a field node consumer to a specific field node.
     *
     * @param fieldNodeAddress the address of the field node
     */
    void subscribeToFieldNode(int fieldNodeAddress);

    /**
     * Unsubscribes a field node consumer from a specific field node.
     *
     * @param fieldNodeAddress the address of the field node
     */
    void unsubscribeFromFieldNode(int fieldNodeAddress);

    /**
     * Sets the state for a specific actuator on a specific field node.
     *
     * @param fieldNodeAddress the address of the field node
     * @param actuatorAddress the address of the actuator
     * @param newState the state to set
     */
    void setActuatorState(int fieldNodeAddress, int actuatorAddress, int newState);

    /**
     * Returns a string representation of the field node information agent.
     *
     * @return a string representation
     */
    String getFieldNodeSourceAsString();
}
