package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.EnvironmentStateModifier;
import no.ntnu.fieldnode.device.Device;
import no.ntnu.exception.ActuatorInvalidStateException;

/**
 * A device able to change the state of an environment.
 */
public interface Actuator extends Device, EnvironmentStateModifier {
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

    /**
     * Adds a listener to the actuator.
     *
     * @param actuatorListener listener to add
     * @return true if successfully added
     */
    boolean addListener(ActuatorListener actuatorListener);

    /**
     * Removes a listener from the actuator.
     *
     * @param actuatorListener listener to remove
     * @return true if successfully removed
     */
    boolean removeListener(ActuatorListener actuatorListener);
}
