package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when encryption key generation fails.
 *
 * @param <C> any message context
 */
public class KeyGenError<C extends MessageContext> extends ErrorMessage<C> {
    /**
     * Creates a new KeyGenError.
     *
     * @param errorDescription the description of the error
     */
    public KeyGenError(String errorDescription) {
        super(NofspSerializationConstants.KEY_GEN_ERROR_CODE, errorDescription);
    }

    /**
     * Creates a new KeyGenError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public KeyGenError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
