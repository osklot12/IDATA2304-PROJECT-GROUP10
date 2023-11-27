package no.ntnu.network.message.response.error;

import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.network.message.serialize.NofspSerializationConstants;

/**
 * An error message for when a field node cannot be reached.
 */
public class FieldNodeUnreachableError extends ErrorMessage<ControlPanelContext> {
    /**
     * Creates a new FieldNodeUnreachableError.
     *
     * @param errorDescription the description of the error
     */
    public FieldNodeUnreachableError(String errorDescription) {
        super(NofspSerializationConstants.FIELD_NODE_UNREACHABLE_CODE, errorDescription);
    }

    /**
     * Creates a new FieldNodeUnreachableError.
     *
     * @param id the message id
     * @param errorDescription the description of the error
     */
    public FieldNodeUnreachableError(int id, String errorDescription) {
        this(errorDescription);

        setId(id);
    }
}
