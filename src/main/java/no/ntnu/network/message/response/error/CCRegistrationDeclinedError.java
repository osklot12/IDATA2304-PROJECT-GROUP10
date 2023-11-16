package no.ntnu.network.message.response.error;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.ControlPanelContext;

/**
 * An error message indicating failed control panel registration.
 */
public class CCRegistrationDeclinedError extends RegistrationDeclinedError implements Message<ControlPanelContext> {
    /**
     * Creates a new CCRegistrationDeclinedResponse.
     *
     * @param description the description of the decline
     */
    public CCRegistrationDeclinedError(String description) {
        super(description);
    }

    @Override
    public void process(ControlPanelContext context) {
        super.commonProcess(context);
    }

    @Override
    public String toString() {
        return getDescription().getString();
    }
}
