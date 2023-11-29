package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A response sent to a successful {@code UnsubscribeFromFieldNodeRequest}, indicating that the control panel
 * has been successfully unsubscribed from the field node.
 */
public class UnsubscribedFromFieldNodeResponse extends StandardProcessingResponseMessage<ControlPanelContext> {
    private final int fieldNodeAddress;

    /**
     * Creates a new UnsubscribedFromFieldNodeResponse.
     *
     * @param fieldNodeAddress the address of the field node to remove
     */
    public UnsubscribedFromFieldNodeResponse(int fieldNodeAddress) {
        super(NofspSerializationConstants.UNSUBSCRIBED_FROM_FIELD_NODE_CODE);
        this.fieldNodeAddress = fieldNodeAddress;
    }

    /**
     * Creates a new UnsubscribedFromFieldNodeResponse.
     *
     * @param id the message id
     * @param fieldNodeAddress the address of the virtual field node to remove
     */
    public UnsubscribedFromFieldNodeResponse(int id, int fieldNodeAddress) {
        this(fieldNodeAddress);
        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ControlPanelContext context) {
        context.removeVirtualFieldNode(fieldNodeAddress);
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this, new ByteSerializableInteger(fieldNodeAddress));
    }

    @Override
    public String toString() {
        return "successfully unsubscribed from field node";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof UnsubscribedFromFieldNodeResponse u)) {
            return false;
        }

        return super.equals(u) && fieldNodeAddress == u.fieldNodeAddress;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodeAddress;

        return result;
    }
}
