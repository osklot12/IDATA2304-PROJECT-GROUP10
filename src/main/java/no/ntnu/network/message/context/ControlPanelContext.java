package no.ntnu.network.message.context;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.ClientAgent;

/**
 * A context for processing control panel messages.
 */
public class ControlPanelContext extends ClientContext {
    private final ControlPanel controlPanel;

    /**
     * Creates a ControlPanelContext.
     *
     * @param agent the communication agent
     * @param controlPanel the control panel to operate on
     */
    public ControlPanelContext(ClientAgent agent, ControlPanel controlPanel) {
        super(agent);
        if (controlPanel == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelContext, because control panel is null");
        }

        this.controlPanel = controlPanel;
    }
}
