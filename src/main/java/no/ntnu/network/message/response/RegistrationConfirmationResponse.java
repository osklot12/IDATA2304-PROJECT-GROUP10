package no.ntnu.network.message.response;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.request.RequestMessage;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A confirmation response to a node registration request, indicating that the client has been
 * registered successfully.
 */
public class RegistrationConfirmationResponse<C extends ClientContext> extends StandardProcessingResponseMessage<C> {
    private final int nodeAddress;

    /**
     * Creates a new RegistrationConfirmationResponse.
     *
     * @param nodeAddress the address assigned to the node
     */
    public RegistrationConfirmationResponse(int nodeAddress) {
        super(NofspSerializationConstants.NODE_REGISTRATION_CONFIRMED_CODE);

        this.nodeAddress = nodeAddress;
    }

    /**
     * Creates a new RegistrationConfirmationResponse.
     *
     * @param messageId the message id
     * @param nodeAddress the address assigned to the node
     */
    public RegistrationConfirmationResponse(int messageId, int nodeAddress) {
        this(nodeAddress);

        setId(messageId);
    }

    /**
     * Returns the node address assigned to the client.
     *
     * @return the node address
     */
    public int getNodeAddress() {
        return nodeAddress;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this, new ByteSerializableInteger(nodeAddress));
    }

    @Override
    protected void handleResponseProcessing(C context) {
        context.setClientNodeAddress(nodeAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RegistrationConfirmationResponse<?> r)) {
            return false;
        }

        return super.equals(r) && nodeAddress == r.nodeAddress;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + nodeAddress;;

        return result;
    }

    @Override
    public String toString() {
        return "successful registration, assigned address " + nodeAddress;
    }
}
