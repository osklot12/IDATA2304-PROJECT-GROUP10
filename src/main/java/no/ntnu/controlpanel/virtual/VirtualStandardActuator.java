package no.ntnu.controlpanel.virtual;

import no.ntnu.broker.VirtualActuatorStateBroker;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A representation of a standard actuator, used for storing data about the actuator it represents.
 */
public class VirtualStandardActuator extends VirtualDevice {
    private final VirtualActuatorStateBroker stateBroker;
    private int state;

    /**
     * Creates a new VirtualStandardActuator.
     *
     * @param deviceClass the class of device
     * @param state the current state of the actuator
     */
    public VirtualStandardActuator(DeviceClass deviceClass, int state) {
        super(deviceClass);

        this.stateBroker = new VirtualActuatorStateBroker();
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
     *
     * @param state state to set
     * @param global true if change is global, false if change is local
     */
    public void setState(int state, boolean global) {
        this.state = state;
        stateBroker.notifyListeners(this, global);
    }

    /**
     * Adds a listener.
     *
     * @param listener listener to add
     * @return true if successfully added
     */
    public boolean addListener(VirtualActuatorListener listener) {
        return stateBroker.addSubscriber(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener listener to remove
     * @return true if successfully removed
     */
    public boolean removeListener(VirtualActuatorListener listener) {
        return stateBroker.removeSubscriber(listener);
    }
}
