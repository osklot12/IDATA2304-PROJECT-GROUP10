package no.ntnu.network.message.response.error;

import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.response.ResponseMessage;

/**
 * A response message indicating that an error has occurred.
 */
public abstract class ErrorMessage extends ResponseMessage {
    private final ByteSerializableString errorDescription;

    /**
     * Creates a new ErrorMessage.
     *
     * @param statusCode the status code
     * @param errorDescription the description of the error
     */
    protected ErrorMessage(int statusCode, String errorDescription) {
        super(statusCode);

        if (errorDescription == null) {
            throw new IllegalArgumentException("Cannot create ErrorMessage, because error description is null");
        }

        this.errorDescription = new ByteSerializableString(errorDescription);
    }

    /**
     * Returns the description of the error.
     *
     * @return the error description
     */
    public ByteSerializableString getDescription() {
        return errorDescription;
    }
}
