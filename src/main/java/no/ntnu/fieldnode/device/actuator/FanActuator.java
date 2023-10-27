package no.ntnu.fieldnode.device.actuator;

import no.ntnu.broker.ActuatorStateBroker;
import no.ntnu.environment.Environment;
import no.ntnu.exception.ActuatorInvalidStateException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * An actuator controlling a fan.
 */
public class FanActuator implements Actuator {
    private final DeviceClass deviceClass;
    private final ActuatorState state;
    private final ActuatorStateBroker stateBroker;
    private Environment environment;

    /**
     * Creates a new FanActuator.
     */
    public FanActuator() {
        this.deviceClass = DeviceClass.A1;
        this.state = new ActuatorState(new int[] {0, 1, 2, 3});
        this.stateBroker = new ActuatorStateBroker();
    }

    @Override
    public void setEnvironment(Environment environment){
        if (!(this.environment == null)) {
            this.environment.removeTemperatureModifier(this);
        }

        if (!(environment == null)) {
            environment.addTemperatureModifier(this);
            this.environment = environment;
        }
    }

    @Override
    public double modifyEnvironmentState(Double value) {
        switch (state.getState()) {
            case 1:
                value-=1;
                break;

            case 2:
                value-=2;
                break;

            case 3:
                value-=3;
                break;

            default:
                break;
        }

        return value;
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

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + deviceClass.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + stateBroker.hashCode();
        result = 31 * result + environment.hashCode();
        return result;
    }
}
