package no.ntnu.network.message.response.error;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.tools.Logger;

/**
 * An error message for when a client registration is declined.
 */
public abstract class RegistrationDeclinedError extends ErrorMessage {
    /**
     * Creates a new RegistrationDeclinedResponse.
     *
     * @param description the description of the decline
     */
    protected RegistrationDeclinedError(String description) {
        super(101, description);
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return new byte[0];
    }

    protected void commonProcess(ClientContext context) {
        if (context.acceptResponse(this)) {
            Logger.error(getDescription().getString());
        }
    }
}
