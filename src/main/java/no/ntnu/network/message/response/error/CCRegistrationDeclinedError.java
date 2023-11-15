package no.ntnu.network.message.response.error;

import no.ntnu.network.message.ControlPanelMessage;
import no.ntnu.network.message.context.ControlPanelContext;

/**
 * An error message indicating failed control panel registration.
 */
public class CCRegistrationDeclinedError extends RegistrationDeclinedError implements ControlPanelMessage {
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
}
