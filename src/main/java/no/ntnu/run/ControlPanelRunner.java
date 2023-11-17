package no.ntnu.run;

import no.ntnu.controlpanel.ControlPanel;
import no.ntnu.network.client.ControlPanelClient;
import no.ntnu.tools.Logger;

public class ControlPanelRunner {
    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        ControlPanelClient client = new ControlPanelClient(controlPanel);
        client.connect("localhost");
    }
}
