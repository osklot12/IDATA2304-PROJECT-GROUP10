package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when an FNSM at the server side cannot be updated.
 */
public class ServerFnsmUpdateRejectedError extends ErrorMessage<FieldNodeContext> {
    /**
     * Creates a new ServerFnsmUpdateRejectedError.
     *
     * @param errorDescription the description of the error
     */
    public ServerFnsmUpdateRejectedError(String errorDescription) {
        super(NofspSerializationConstants.SERVER_FNSM_UPDATE_REJECTED_CODE, errorDescription);
    }

    /**
     * Creates a new ServerFnsmUpdateRejectedError.
     *
     * @param messageId the message id
     * @param errorDescription the description of the error
     */
    public ServerFnsmUpdateRejectedError(int messageId, String errorDescription) {
        this(errorDescription);

        setId(messageId);
    }
}
