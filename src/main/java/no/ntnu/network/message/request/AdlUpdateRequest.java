package no.ntnu.network.message.request;

import no.ntnu.exception.NoSuchDeviceException;
import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.response.AdlUpdatedResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.error.AdlUpdateRejectedError;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Set;

/**
 * A request sent from the central server to a field node, requesting to update the active device list (ADL) of
 * the field node.
 */
public class AdlUpdateRequest extends StandardProcessingRequestMessage<FieldNodeContext> {
    private final Set<Integer> adlUpdates;

    /**
     * Creates a new AdlUpdateRequest.
     *
     * @param adlUpdates the adl updates to request
     */
    public AdlUpdateRequest(Set<Integer> adlUpdates) {
        super(NofspSerializationConstants.ADL_UPDATE_COMMAND);

        this.adlUpdates = adlUpdates;
    }

    /**
     * Creates a new AdlUpdateRequest.
     *
     * @param id the message id
     * @param adlUpdates the adl updates to request
     */
    public AdlUpdateRequest(int id, Set<Integer> adlUpdates) {
        this(adlUpdates);

        setId(id);
    }

    @Override
    protected ResponseMessage executeAndCreateResponse(FieldNodeContext context) {
        ResponseMessage response = null;

        try {
            context.updateAdl(adlUpdates);
            response = new AdlUpdatedResponse();
        } catch (NoSuchDeviceException e) {
            response = new AdlUpdateRejectedError(e.getMessage());
        }

        return response;
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitRequestMessage(this, DataTypeConverter.getSerializableSetOfIntegers(adlUpdates));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AdlUpdateRequest a)) {
            return false;
        }

        return super.equals(a) && adlUpdates.equals(a.adlUpdates);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + adlUpdates.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "requesting to update ADL.";
    }
}
