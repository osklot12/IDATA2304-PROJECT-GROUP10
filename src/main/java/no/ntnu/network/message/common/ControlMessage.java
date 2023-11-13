package no.ntnu.network.message.common;

import no.ntnu.network.message.Message;

/**
 * A message sent from one node to another for control purposes. Control messages can be categorized into two categories:
 * request messages and response messages.
 * <p/>
 * All control messages are assigned a unique ID used to map requests to responses. These IDs are only unique for that
 * connection, as different connections can use the same IDs.
 */
public abstract class ControlMessage implements Message {
    private final ByteSerializableInteger id;

    /**
     * Creates a new ControlMessage.
     *
     * @param id the id for the message. Must be a non-negative integer
     */
    protected ControlMessage(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot create ControlMessage, because message ID is negative");
        }

        this.id = new ByteSerializableInteger(id);
    }

    /**
     * Returns the ID for the control message.
     *
     * @return the message ID
     */
    public ByteSerializableInteger getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ControlMessage r)) {
            return false;
        }

        return r.getId().equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + id.hashCode();

        return result;
    }
}
