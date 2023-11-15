package no.ntnu.network.message.response;

import no.ntnu.network.message.ControlPanelMessage;
import no.ntnu.network.message.context.ControlPanelContext;
import no.ntnu.tools.Logger;

/**
 * Registration confirmation response for a control panel client.
 */
public class CCRegistrationConfirmationResponse extends RegistrationConfirmationResponse implements ControlPanelMessage {
    /**
     * Creates a new CCRegistrationConfirmationResponse.
     *
     * @param nodeAddress the address for the node
     */
    public CCRegistrationConfirmationResponse(int nodeAddress) {
        super(nodeAddress);
    }

    /**
     * Creates a new CCRegistrationConfirmationResponse.
     *
     * @param messageId the message id
     * @param nodeAddress the address for the node
     */
    public CCRegistrationConfirmationResponse(int messageId, int nodeAddress) {
        super(messageId, nodeAddress);
    }

    @Override
    public String toString() {
        return "Control panel has been registered with address " + getNodeAddress().toString();
    }

    @Override
    public void process(ControlPanelContext context) {
        super.commonProcess(context);
    }
}
