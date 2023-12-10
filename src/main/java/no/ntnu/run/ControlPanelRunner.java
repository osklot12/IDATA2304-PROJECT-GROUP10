package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.gui.ControlPanelConnector;
import no.ntnu.gui.ControlPanelGui;
import no.ntnu.network.client.ControlPanelClient;

public class ControlPanelRunner {
    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        ControlPanelConnector.start(controlPanel);
    }
}
