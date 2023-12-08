package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error response to a {@code SyncEncryptionRequest}, indicating that the client could not synchronize for
 * encryption.
 */
public class SyncEncryptionRejectedError extends ErrorMessage<ServerContext> {
    /**
     * Creates a new SyncEncryptionRejectedError.
     *
     * @param errorDescription the description of the error
     */
    public SyncEncryptionRejectedError(String errorDescription) {
        super(NofspSerializationConstants.SYNC_ENCRYPTION_REJECTED_CODE, errorDescription);
    }

    /**
     * Creates a new SyncEncryptionRejectedError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public SyncEncryptionRejectedError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
