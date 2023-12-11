package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.gui.ControlPanelConnector;

/**
 * Runner for a control panel.
 * The class runs a control panel connector for the control panel, effectively connecting the control panel
 * to a central server and displaying its GUI.
 */
public class ControlPanelRunner {
    /**
     * The main starting point for a control panel connector.
     *
     * @param args console line arguments
     */
    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        ControlPanelConnector.start(controlPanel);
    }
}
