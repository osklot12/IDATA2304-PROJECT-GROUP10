package no.ntnu.network.message.context;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.ClientCommunicationAgent;

/**
 * A context for processing messages for a control panel.
 * The context provides access to objects that control messages needs for processing.
 */
public class ControlPanelContext extends ClientContext {
    private final ControlPanel controlPanel;

    /**
     * Creates a ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel to operate on
     */
    public ControlPanelContext(ClientCommunicationAgent agent, ControlPanel controlPanel) {
        super(agent);
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelContext, because control panel is null");
        }

        this.controlPanel = controlPanel;
    }
}
