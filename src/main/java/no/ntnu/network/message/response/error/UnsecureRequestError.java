package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when a request cannot be accepted due to security issues.
 *
 * @param <C> any message context
 */
public class UnsecureRequestError<C extends MessageContext> extends ErrorMessage<C> {
    /**
     * Creates a new UnsecureRequestError.
     *
     * @param errorDescription the description of the error
     */
    public UnsecureRequestError(String errorDescription) {
        super(NofspSerializationConstants.UNSECURE_REQUEST_ERROR_CODE, errorDescription);
    }

    /**
     * Creates a new UnsecureRequestError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public UnsecureRequestError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
