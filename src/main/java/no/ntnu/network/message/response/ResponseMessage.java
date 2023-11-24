package no.ntnu.network.message.response;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.MessageContext;

/**
 * A message sent from one node to another, responding to a request message.
 * The class provides standard response message processing for any class extending it.
 * Subclasses of this class should implement their processing logic in the {@code handleResponseProcessing()} method,
 * and not in the {@code process()} method provided by the {@code Message} interface.
 *
 * @param <C> any message context
 */
public abstract class ResponseMessage<C extends MessageContext> extends ControlMessage implements Message<C> {
    private final ByteSerializableInteger statusCode;

    /**
     * Creates a new ResponseMessage.
     *
     * @param statusCode the status code
     */
    protected ResponseMessage(int statusCode) {
        this.statusCode = new ByteSerializableInteger(statusCode);
    }

    /**
     * Returns the status code.
     *
     * @return the status code
     */
    public ByteSerializableInteger getStatusCode() {
        return statusCode;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ResponseMessage<?> r)) {
            return false;
        }

        return super.equals(r) && r.getStatusCode().equals(statusCode);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + statusCode.hashCode();

        return result;
    }
}
