package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ServerContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when the server tries to access a virtual device that does not exist at a control panel.
 */
public class NoSuchVirtualDeviceError extends ErrorMessage<ServerContext> {
    /**
     * Creates a new NoSuchVirtualDeviceError.
     *
     * @param errorDescription the description of the error
     */
    public NoSuchVirtualDeviceError(String errorDescription) {
        super(NofspSerializationConstants.NO_SUCH_VIRTUAL_DEVICE_CODE, errorDescription);
    }

    /**
     * Creates a new NoSuchVirtualDeviceError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public NoSuchVirtualDeviceError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
