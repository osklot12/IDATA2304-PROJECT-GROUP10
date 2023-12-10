package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error for when the interaction with a field node device fails.
 */
public class DeviceInteractionFailedError extends ErrorMessage<ServerContext> {
    /**
     * Creates a new DeviceInteractionFailedError.
     *
     * @param errorDescription the description of the error
     */
    public DeviceInteractionFailedError(String errorDescription) {
        super(NofspSerializationConstants.DEVICE_INTERACTION_FAILED_CODE, errorDescription);
    }

    /**
     * Creates a new DeviceInteractionFailedError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public DeviceInteractionFailedError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
