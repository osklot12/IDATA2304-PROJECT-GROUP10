package no.ntnu.network.message.response;

import no.ntnu.network.message.context.FieldNodeContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * A response to a {@code ActuatorNotificationRequest}, indicating that the server FNSM has been updated
 * accordingly.
 */
public class ServerFnsmUpdatedResponse extends StandardProcessingResponseMessage<FieldNodeContext> {
    /**
     * Creates a new ServerFnsmUpdatedResponse.
     */
    public ServerFnsmUpdatedResponse() {
        super(NofspSerializationConstants.SERVER_FNSM_UPDATED_CODE);
    }

    /**
     * Creates a new ServerFnsmUpdatedResponse.
     *
     * @param id the message id
     */
    public ServerFnsmUpdatedResponse(int id) {
        this();

        setId(id);
    }

    @Override
    protected void handleResponseProcessing(FieldNodeContext context) {
        // no further processing required
    }

    @Override
    public String toString() {
        return "FNSM was successfully updated";
    }
}
