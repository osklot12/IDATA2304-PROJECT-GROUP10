package no.ntnu.fieldnode.device.actuator;

import no.ntnu.broker.ActuatorStateBroker;
import no.ntnu.environment.Environment;
import no.ntnu.environment.EnvironmentStateModifier;
import no.ntnu.exception.ActuatorInvalidStateException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * A class representing a standard actuator for an environment.
 */
public abstract class StandardActuator implements Actuator, EnvironmentStateModifier {
    protected final DeviceClass deviceClass;
    protected final ActuatorState state;
    protected final ActuatorStateBroker stateBroker;
    protected Environment environment;

    /**
     * Creates a new StandardActuator.
     *
     * @param deviceClass the class of the device
     * @param states the allowed states for the actuator
     */
    public StandardActuator(DeviceClass deviceClass, int[] states) {
        if (null == deviceClass) {
            throw new IllegalArgumentException("Cannot create Actuator, because device class is null.");
        }

        if (states.length == 0) {
            throw new IllegalArgumentException("Cannot create Actuator, because it needs at least one allowed state.");
        }

        this.deviceClass = deviceClass;
        this.state = new ActuatorState(states);
        this.stateBroker = new ActuatorStateBroker();
    }

    @Override
    public int getState() {
        return state.getState();
    }

    @Override
    public void setState(int state) throws ActuatorInvalidStateException {
        this.state.setState(state);
        stateBroker.notifyListeners(this);
    }

    @Override
    public DeviceClass getDeviceClass() {
        return deviceClass;
    }

    @Override
    public boolean connectToFieldNode(FieldNode fieldNode) {
        return stateBroker.addSubscriber(fieldNode);
    }

    @Override
    public boolean disconnectFromFieldNode(FieldNode fieldNode) {
        return stateBroker.removeSubscriber(fieldNode);
    }
}
