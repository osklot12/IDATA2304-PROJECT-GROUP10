package no.ntnu.network.message;

import no.ntnu.network.message.context.ControlPanelContext;

/**
 * A message processed by a control panel.
 */
public interface ControlPanelMessage extends ClientMessage<ControlPanelContext> {
    /**
     * Processes the message.
     *
     * @param context the message context
     */
    void process(ControlPanelContext context);
}
