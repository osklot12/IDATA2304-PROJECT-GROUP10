package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * The class provides standard request message processing for any class extending it.
 * Subclasses of this class should implement their processing logic in the {@code executeAndCreateResponse()} method,
 * and not in the {@code process()} method provided by the {@code Message} interface.
 *
 * @param <C> the message context used for processing
 */
public abstract class StandardProcessingRequestMessage<C extends MessageContext> extends RequestMessage implements Message<C> {
    /**
     * Creates a new StandardProcessingRequestMessage.
     *
     * @param command the command for the request
     */
    protected StandardProcessingRequestMessage(String command) {
        super(command);
    }

    @Override
    public void process(C context) throws IOException {
        // logs the receiving of the request
        context.logReceivingRequest(this);

        // executes logic and gets response created by subclass implementation
        ResponseMessage response = executeAndCreateResponse(context);

        // sets the same id for the response and sends it back
        setResponseId(response);
        context.respond(response);
    }

    /**
     * Custom processing logic specific for the request.
     * An appropriate response is made and returned.
     *
     * @param context the context to process on
     * @return an appropriate response
     */
    protected abstract ResponseMessage executeAndCreateResponse(C context);
}
