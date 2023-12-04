package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.DataTypeConverter;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

import java.io.IOException;
import java.util.Set;

/**
 * A response to a successful ADL update request, indicating that the desired updates has been complete.
 */
public class AdlUpdatedResponse extends StandardProcessingResponseMessage<ServerContext> {
    private final Set<Integer> updatedAdl;

    /**
     * Creates a new AdlUpdateResponse.
     *
     * @param updatedAdl the updated adl
     */
    public AdlUpdatedResponse(Set<Integer> updatedAdl) {
        super(NofspSerializationConstants.ADL_UPDATED_CODE);
        if (updatedAdl == null) {
            throw new IllegalArgumentException("Cannot create AdlUpdatedResponse, because updatedAdl is null.");
        }

        this.updatedAdl = updatedAdl;
    }

    /**
     * Creates a new AdlUpdatedResponse.
     *
     * @param id the message id
     * @param updatedAdl the updated adl
     */
    public AdlUpdatedResponse(int id, Set<Integer> updatedAdl) {
        this(updatedAdl);

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ServerContext context) {
        context.updateLocalAdl(updatedAdl);
    }

    @Override
    public Tlv accept(ByteSerializerVisitor visitor) throws IOException {
        return visitor.visitResponseMessage(this, DataTypeConverter.getSerializableSetOfIntegers(updatedAdl));
    }

    @Override
    public String toString() {
        return "ADL was successfully updated.";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AdlUpdatedResponse a)) {
            return false;
        }

        return super.equals(a) && updatedAdl.equals(a.updatedAdl);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + updatedAdl.hashCode();

        return result;
    }
}
