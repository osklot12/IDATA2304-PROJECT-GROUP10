package no.ntnu.network.message.response.error;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

import java.io.IOException;

/**
 * An error message for when a subscription to a field node fails.
 *
 * @param <C> the client context for message processing
 */
public class SubscriptionError<C extends ClientContext> extends ErrorMessage implements Message<C> {
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

    @Override
    public void process(C context) throws IOException {
        if (context.acceptResponse(this)) {
            context.logReceivingResponse(this);
        }
    }
}
