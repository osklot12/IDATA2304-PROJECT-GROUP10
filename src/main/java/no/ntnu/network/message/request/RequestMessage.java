package no.ntnu.network.message.request;

import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.common.ControlMessage;

/**
 * A Message used for requesting a service.
 */
public abstract class RequestMessage extends ControlMessage {
    protected final ByteSerializableString command;

    /**
     * Creates a new RequestMessage.
     *
     * @param messageId the message ID
     * @param command the command for the request
     */
    protected RequestMessage(int messageId, String command) {
        super(messageId);
        this.command = new ByteSerializableString(command);
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
