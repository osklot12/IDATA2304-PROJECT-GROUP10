package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ClientContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when a client registration is declined.
 */
public class RegistrationDeclinedError<C extends ClientContext> extends ErrorMessage<C> {
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
}
