package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.composite.ByteSerializable;

/**
 * A message sent from one node to another for control purposes. Control messages can be categorized into two categories:
 * request messages and response messages.
 * <p/>
 * All control messages are assigned a unique ID used to map requests to responses. These IDs are only unique for that
 * connection, as different connections can use the same IDs.
 */
public abstract class ControlMessage implements ByteSerializable {
    private ByteSerializableInteger id;

    /**
     * Creates a new ControlMessage.
     */
    protected ControlMessage() {
        this.id = new ByteSerializableInteger(0);
    }

    /**
     * Returns the ID for the control message.
     *
     * @return the message ID
     */
    public ByteSerializableInteger getId() {
        return id;
    }

    /**
     * Sets the ID for the control message.
     *
     * @param id the message ID
     */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Cannot set ID, because it is negative.");
        }

        this.id = new ByteSerializableInteger(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ControlMessage c)) {
            return false;
        }

        return c.getId().equals(id);
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + id.hashCode();

        return result;
    }
}
