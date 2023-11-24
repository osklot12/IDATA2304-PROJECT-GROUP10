package no.ntnu.network.message.response.error;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.response.RegistrationConfirmationResponse;
import no.ntnu.network.message.response.ResponseMessage;
import no.ntnu.network.message.response.StandardProcessingResponseMessage;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;

/**
 * A response message indicating that an error has occurred.
 */
public abstract class ErrorMessage<C extends MessageContext> extends StandardProcessingResponseMessage<C> {
    private final ByteSerializableString errorDescription;

    /**
     * Creates a new ErrorMessage.
     *
     * @param statusCode the status code
     * @param errorDescription the description of the error
     */
    protected ErrorMessage(int statusCode, String errorDescription) {
        super(statusCode);

        if (errorDescription == null) {
            throw new IllegalArgumentException("Cannot create ErrorMessage, because error description is null");
        }

        this.errorDescription = new ByteSerializableString(errorDescription);
    }

    /**
     * Returns the description of the error.
     *
     * @return the error description
     */
    public ByteSerializableString getDescription() {
        return errorDescription;
    }

    @Override
    protected void handleResponseProcessing(C context) {
        // error messages usually do not require further processing
        // the ones that do can override this method
    }

    @Override
    public byte[] accept(ByteSerializerVisitor visitor) throws SerializationException {
        return visitor.visitResponseMessage(this, getDescription());
    }

    @Override
    public String toString() {
        return getDescription().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ErrorMessage<?> e)) {
            return false;
        }

        return super.equals(e) && errorDescription.equals(e.errorDescription);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + errorDescription.hashCode();

        return result;
    }
}
