package no.ntnu.network.message.request;

import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.response.ResponseMessage;

/**
 * A message sent from one node to another, requesting a service.
 */
public abstract class RequestMessage extends ControlMessage {
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
        response.setId(getId());
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RequestMessage r)) {
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
