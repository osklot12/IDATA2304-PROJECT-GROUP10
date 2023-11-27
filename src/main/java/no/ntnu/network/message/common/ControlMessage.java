package no.ntnu.network.message.common;

import no.ntnu.network.message.serialize.ByteSerializable;

/**
 * A message sent from one node to another for control purposes. Control messages can be categorized into two categories:
 * request messages and response messages.
 * <p/>
 * All control messages are assigned a unique ID used to map requests to responses. These IDs are only unique for that
 * connection, as different connections can use the same IDs.
 */
public abstract class ControlMessage implements ByteSerializable {
    private int id;

    /**
     * Creates a new ControlMessage.
     */
    protected ControlMessage() {
        this.id = 0;
    }

    /**
     * Returns the message ID.
     *
     * @return message id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the message ID in a serializable format.
     *
     * @return the message ID
     */
    public ByteSerializableInteger getSerializableId() {
        return new ByteSerializableInteger(id);
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

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ControlMessage c)) {
            return false;
        }

        return super.equals(c) && id == c.id;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + id;

        return result;
    }
}
