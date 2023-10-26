package no.ntnu.fieldnode.device.actuator;

import no.ntnu.fieldnode.device.Device;
import no.ntnu.exception.ActuatorInvalidStateException;

/**
 * A device able to change the state of an environment.
 */
public interface Actuator extends Device {
    /**
     * Returns the state of the actuator.
     *
     * @return state of actuator
     */
    int getState();

    /**
     * Sets the state of the actuator.
     *
     * @throws ActuatorInvalidStateException throws an exception if the state is invalid
     */
    void setState(int state) throws ActuatorInvalidStateException;
}
