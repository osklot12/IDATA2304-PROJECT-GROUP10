package no.ntnu.controlpanel.virtual.sensor;

/**
 * A listener listening for a change in data held by a virtual sdu sensor.
 */
public interface VirtualSDUSensorListener {
    /**
     * Triggers when new sdu data is stored by a virtual sdu sensor.
     *
     * @param sensorAddress the address of the sensor that captured the data
     */
    void newSduData(int sensorAddress);
}
