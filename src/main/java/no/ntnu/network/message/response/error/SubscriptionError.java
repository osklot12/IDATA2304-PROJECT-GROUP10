package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when a subscription to a field node fails.
 *
 * @param <C> the client context for message processing
 */
public class SubscriptionError<C extends ClientContext> extends ErrorMessage<C> {
    /**
     * Creates a new SubscriptionError.
     *
     * @param errorDescription the description of the error
     */
    public SubscriptionError(String errorDescription) {
        super(NofspSerializationConstants.SUBSCRIPTION_FAILED_CODE, errorDescription);
    }

    /**
     * Creates a new SubscriptionError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public SubscriptionError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
