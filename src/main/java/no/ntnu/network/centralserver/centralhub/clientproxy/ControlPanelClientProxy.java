package no.ntnu.network.centralserver.centralhub.clientproxy;

import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.ControlCommAgent;
import no.ntnu.network.DataCommAgent;
import no.ntnu.network.message.sensordata.SensorDataMessage;

import java.io.IOException;
import java.util.Set;

/**
 * A proxy for a control panel client, used for storing information about the control panel and interacting with it.
 */
public class ControlPanelClientProxy extends ClientProxy {
    private final DataCommAgent dataAgent;
    private final Set<DeviceClass> compatibilityList;

    /**
     * Creates a new ControlPanelClientProxy.
     *
     * @param controlAgent the control communication agent
     * @param dataAgent the sensor data communication agent
     */
    public ControlPanelClientProxy(ControlCommAgent controlAgent, DataCommAgent dataAgent, Set<DeviceClass> compatibilityList) {
        super(controlAgent);
        if (dataAgent == null) {
            throw new IllegalArgumentException("Cannot create ControlPanelClientProxy, because dataAgent is null.");
        }

        this.dataAgent = dataAgent;
        this.compatibilityList = compatibilityList;
    }

    /**
     * Sends a sensor data message to the control panel.
     *
     * @param message the sensor data message to send
     * @throws IOException thrown if an I/O exception occurs
     */
    public void sendSensorData(SensorDataMessage message) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("Cannot send sensor data message, because message is null.");
        }

        dataAgent.sendSensorData(message);
    }

    /**
     * Returns the compatibility list for the control panel client proxy.
     *
     * @return compatibility list
     */
    public Set<DeviceClass> getCompatibilityList() {
        return compatibilityList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ControlPanelClientProxy c)) {
            return false;
        }

        return super.equals(c) && dataAgent.equals(c.dataAgent) && compatibilityList.equals(c.compatibilityList);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result * 31 + dataAgent.hashCode();
        result = result * 31 + compatibilityList.hashCode();

        return result;
    }
}
