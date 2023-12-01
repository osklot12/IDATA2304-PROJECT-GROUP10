package no.ntnu.network.message.request;

import no.ntnu.exception.ClientRegistrationException;
import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.RegistrationDeclinedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.representation.FieldNodeInformation;

import java.util.Map;

/**
 * A request to register a {@code FieldNode} at the central server.
 */
public class RegisterFieldNodeRequest extends StandardProcessingRequestMessage<ServerContext> {
    private final FieldNodeInformation fieldNodeInformation;

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param fieldNodeInformation information about the field node
     */
    public RegisterFieldNodeRequest(FieldNodeInformation fieldNodeInformation) {
        super(NofspSerializationConstants.REGISTER_FIELD_NODE_COMMAND);
        if (fieldNodeInformation == null) {
            throw new IllegalArgumentException("Cannot create RegisterFieldNodeRequest, because fieldNodeInformation" +
                    " is null.");
        }

        this.fieldNodeInformation = fieldNodeInformation;
    }

    /**
     * Creates a new RegisterFieldNodeRequest.
     *
     * @param id the message id
     * @param fieldNodeInformation information about the field node
     */
    public RegisterFieldNodeRequest(int id, FieldNodeInformation fieldNodeInformation) {
        this(fieldNodeInformation);

        setId(id);
    }

    /**
     * Returns the field node information.
     *
     * @return the field node information
     */
    public FieldNodeInformation getFieldNodeInformation() {
        return fieldNodeInformation;
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(ServerContext context) {
        ResponseMessage response = null;

        try {
            int clientAddress = context.registerFieldNodeClient(fieldNodeInformation);
            response = new RegistrationConfirmationResponse<>(clientAddress);
        } catch (ClientRegistrationException e) {
            response = new RegistrationDeclinedError<>(e.getMessage());
        }

        return response;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, DataTypeConverter.getSerializableFnst(fieldNodeInformation.fnst()),
                DataTypeConverter.getSerializableFnsm(fieldNodeInformation.fnsm()),
                new ByteSerializableString(fieldNodeInformation.name()));
    }

    @Override
    public String toString() {
        return "requesting to register field node";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RegisterFieldNodeRequest r)) {
            return false;
        }

        return super.equals(r) && fieldNodeInformation.equals(r.fieldNodeInformation);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + fieldNodeInformation.hashCode();

        return result;
    }
}
