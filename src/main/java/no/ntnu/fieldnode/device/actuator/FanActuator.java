package no.ntnu.fieldnode.device.actuator;

import no.ntnu.broker.ActuatorStateBroker;
import no.ntnu.environment.Environment;
import no.ntnu.exception.ActuatorInvalidStateException;
import no.ntnu.fieldnode.FieldNode;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * An actuator controlling a fan.
 */
public class FanActuator extends StandardActuator {
    /**
     * Creates a new FanActuator.
     */
    public FanActuator() {
        super(DeviceClass.A1, new int[] {0, 1, 2, 3});
    }

    @Override
    public void setEnvironment(Environment environment){
        if (this.environment != null) {
            this.environment.removeTemperatureModifier(this);
        }

        if (environment != null) {
            environment.addTemperatureModifier(this);
            this.environment = environment;
        }
    }

    @Override
    public double modifyEnvironmentState(Double value) {
        switch (state.getState()) {
            case 1:
                value -= 2;
                break;

            case 2:
                value -= 4;
                break;

            case 3:
                value -= 6;
                break;

            default:
                break;
        }

        return value;
    }
}
