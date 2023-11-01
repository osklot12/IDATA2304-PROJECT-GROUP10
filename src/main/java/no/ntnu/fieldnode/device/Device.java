package no.ntnu.fieldnode.device;

import no.ntnu.environment.Environment;
import no.ntnu.exception.EnvironmentNotSupportedException;
import no.ntnu.fieldnode.FieldNode;

/**
 * A device interacting with an environment.
 */
public interface Device {
    /**
     * Returns the class of the device.
     *
     * @return class of device
     */
    DeviceClass getDeviceClass();

    /**
     * Sets an environment for the device.
     *
     * @param environment the environment to set
     * @throws EnvironmentNotSupportedException throws an exception if environment is not supported
     */
    void setEnvironment(Environment environment);
}
