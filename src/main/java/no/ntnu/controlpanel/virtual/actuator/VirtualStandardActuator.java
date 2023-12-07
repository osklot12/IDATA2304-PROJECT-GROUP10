package no.ntnu.controlpanel.virtual.actuator;

import no.ntnu.controlpanel.virtual.VirtualDevice;
import no.ntnu.fieldnode.device.DeviceClass;

import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a standard actuator, used for storing data about the actuator it represents.
 */
public class VirtualStandardActuator extends VirtualDevice {
    private final Set<VActuatorListener> listeners;
    private int state;

    /**
     * Creates a new VirtualStandardActuator.
     *
     * @param deviceClass the class of device
     * @param state       the current state of the actuator
     */
    public VirtualStandardActuator(DeviceClass deviceClass, int state) {
        super(deviceClass);

        this.listeners = new HashSet<>();
        this.state = state;
    }

    /**
     * Returns the state of the virtual actuator.
     *
     * @return state of virtual actuator
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the state for the virtual standard actuator.
     * The change will be advertised to all listeners except for the source.
     *
     * @param state  state to set
     * @param source the source of the change
     */
    public void setState(int state, Object source) {
        this.state = state;
        listeners.forEach(listener ->  {
            // does not advertise the event to the source of the event
            if (!listener.getVActuatorEventDestination().equals(source)) {
                listener.virtualActuatorStateChanged();
            }
        });
    }

    /**
     * Adds a listener to listen for the event of changes in the state of the actuator.
     *
     * @param listener the listener to add
     */
    public void addListener(VActuatorListener listener) {
        listeners.add(listener);
    }
}
