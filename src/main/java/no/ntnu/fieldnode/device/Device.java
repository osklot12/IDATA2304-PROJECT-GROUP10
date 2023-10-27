package no.ntnu.fieldnode.device;

import no.ntnu.environment.Environment;
import no.ntnu.exception.EnvironmentNotSupportedException;
import no.ntnu.fieldnode.FieldNode;

/**
 * A device connected to a field node.
 */
public interface Device {
    /**
     * Returns the class of the device.
     *
     * @return class of device
     */
    DeviceClass getDeviceClass();

    /**
     * 'Connects' the device to a field node, allowing the device to push notifications to the field node.
     *
     * @param fieldNode the field node to connect to
     * @return true is successfully connected, false on error
     */
    boolean connectToFieldNode(FieldNode fieldNode);

    /**
     * 'Disconnects' the device from a field node, stopping the device from pushing notifications to the field node.
     *
     * @param fieldNode the field node to disconnect from
     * @return true if successfully disconnected, false on error
     */
    boolean disconnectFromFieldNode(FieldNode fieldNode);

    /**
     * Sets an environment for the device.
     *
     * @param environment the environment to set
     * @throws EnvironmentNotSupportedException throws an exception if environment is not supported
     */
    void setEnvironment(Environment environment);
}
