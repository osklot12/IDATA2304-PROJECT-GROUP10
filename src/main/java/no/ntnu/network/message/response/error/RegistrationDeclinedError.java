package no.ntnu.network.message.response.error;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

import java.io.IOException;

/**
 * An error message for when a client registration is declined.
 */
public class RegistrationDeclinedError<C extends ClientContext> extends ErrorMessage implements Message<C> {
    /**
     * Creates a new RegistrationDeclinedResponse.
     *
     * @param description the description of the decline
     */
    public RegistrationDeclinedError(String description) {
        super(NofspSerializationConstants.NODE_REGISTRATION_DECLINED_CODE, description);
    }

    /**
     * Creates a new RegistrationDeclinedResponse.
     *
     * @param id the message id
     * @param description the description of the decline
     */
    public RegistrationDeclinedError(int id, String description) {
        this(description);

        setId(id);
    }

    @Override
    public void process(C context) throws IOException {
        if (context.acceptResponse(this)) {
            context.logReceivingResponse(this);
        }
    }
}
