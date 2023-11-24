package no.ntnu.network.message.request;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.response.ResponseMessage;

import java.io.IOException;

/**
 * A message sent from one node to another, requesting a service.
 * The class provides standard request processing for any class extending it.
 * Subclasses of this class should implement their processing logic in the {@code executeAndCreateResponse()} method,
 * and not in the {@code process()} method provided by the {@code Message} interface.
 */
public abstract class RequestMessage<C extends MessageContext> extends ControlMessage implements Message<C> {
    protected final ByteSerializableString command;

    /**
     * Creates a new RequestMessage.
     *
     * @param command the command for the request
     */
    protected RequestMessage(String command) {
        super();
        this.command = new ByteSerializableString(command);
    }

    /**
     * Sets the response ID to the same ID as this request.
     *
     * @param response response to set id for
     */
    protected void setResponseId(ResponseMessage response) {
        response.setId(getId().getInteger());
    }

    /**
     * Returns the command for the request message.
     *
     * @return the command
     */
    public ByteSerializableString getCommand() {
        return command;
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
     * Custom processing logic specific for each request.
     * An appropriate response is made and returned.
     *
     * @param context the context to process on
     * @return an appropriate response
     */
    protected abstract ResponseMessage executeAndCreateResponse(C context);

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RequestMessage<?> r)) {
            return false;
        }

        return super.equals(r) && r.getCommand().equals(command);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + command.hashCode();

        return result;
    }
}
