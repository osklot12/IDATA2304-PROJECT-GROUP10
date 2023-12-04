package no.ntnu.network.message.response;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a successful ADL update request, indicating that the desired updates has been complete.
 */
public class AdlUpdatedResponse extends StandardProcessingResponseMessage<ServerContext> {
    /**
     * Creates a new AdlUpdateResponse.
     */
    public AdlUpdatedResponse() {
        super(NofspSerializationConstants.ADL_UPDATED_CODE);
    }

    /**
     * Creates a new AdlUpdatedResponse.
     *
     * @param id the message id
     */
    public AdlUpdatedResponse(int id) {
        this();

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(ServerContext context) {
        // TODO: handle further processing
    }

    @Override
    public String toString() {
        return "ADL was successfully updated.";
    }
}
