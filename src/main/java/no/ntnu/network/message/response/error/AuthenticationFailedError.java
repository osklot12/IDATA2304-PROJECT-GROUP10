package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when the central server cannot perform a request due to the client not being authenticated.
 *
 * @param <C> any client context for message processing
 */
public class AuthenticationFailedError<C extends ClientContext> extends ErrorMessage<C> {
    /**
     * Creates a new AuthenticationFailedError.
     *
     * @param errorDescription the description of the error
     */
    public AuthenticationFailedError(String errorDescription) {
        super(NofspSerializationConstants.AUTHENTICATION_FAILED_CODE, errorDescription);
    }

    /**
     * Creates a new AuthenticationFailedError.
     */
    public AuthenticationFailedError() {
        this("cannot accept request, because client is not registered.");
    }

    /**
     * Creates a new AuthenticationFailedError.
     *
     * @param id the message id
     */
    public AuthenticationFailedError(int id) {
        this(id, "Cannot accept request, because client is not registered.");
    }

    /**
     * Creates a new AuthenticationFailedError.
     *
     * @param id the message ID
     * @param errorDescription the description of the error
     */
    public AuthenticationFailedError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
