package no.ntnu.controlpanel.virtual.actuator;

/**
 * Addresses the relation between an actuator and a listener, helping the listener receive the address on events.
 *
 * @param address the address to give the relation
 * @param listener the listener of the actuator
 */
public record ActuatorRelationAddress(int address, AddressedVActuatorListener listener) implements VActuatorListener {
    @Override
    public void virtualActuatorStateChanged() {
        listener.virtualActuatorStateChanged(address);
    }

    @Override
    public Object getVActuatorEventDestination() {
        return listener;
    }
}
