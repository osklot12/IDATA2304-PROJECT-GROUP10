package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when an ADL update is rejected.
 */
public class AdlUpdateRejectedError extends ErrorMessage<ServerContext> {
    /**
     * Creates a new AdlUpdateRejectedError.
     *
     * @param errorDescription the description of the error
     */
    public AdlUpdateRejectedError(String errorDescription) {
        super(NofspSerializationConstants.ADL_UPDATE_REJECTED_CODE, errorDescription);
    }

    /**
     * Creates a new AdlUpdateRejectedError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public AdlUpdateRejectedError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
