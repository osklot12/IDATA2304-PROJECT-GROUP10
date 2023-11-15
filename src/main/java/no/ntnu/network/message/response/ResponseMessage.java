package no.ntnu.network.message.response;

import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ControlMessage;
import no.ntnu.network.message.context.MessageContext;

/**
 * A message sent from one node to another, responding to a request message.
 */
public abstract class ResponseMessage extends ControlMessage {
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ResponseMessage r)) {
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
