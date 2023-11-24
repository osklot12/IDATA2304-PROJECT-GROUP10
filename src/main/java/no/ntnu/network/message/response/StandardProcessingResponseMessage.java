package no.ntnu.network.message.response;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;

/**
 * The class provides standard response message processing for any class extending it.
 * Subclasses of this class should implement their processing logic in the {@code handleResponseProcessing()} method,
 * and not in the {@code process()} method provided by the {@code Message} interface.
 *
 * @param <C> the message context used for processing
 */
public abstract class StandardProcessingResponseMessage<C extends MessageContext> extends ResponseMessage implements Message<C> {
    /**
     * Creates a new StandardProcessingResponseMessage.
     *
     * @param statusCode the status code
     */
    protected StandardProcessingResponseMessage(int statusCode) {
        super(statusCode);
    }

    @Override
    public void process(C context) {
        // only acknowledges the response if the response is accepted
        if (context.acceptResponse(this)) {
            context.logReceivingResponse(this);
            handleResponseProcessing(context);
        }
    }

    /**
     * Handles processing specific for this response.
     *
     * @param context the context to process on
     */
    protected abstract void handleResponseProcessing(C context);
}
