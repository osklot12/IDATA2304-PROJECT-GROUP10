package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * An actuator controlling a dehumidifier.
 */
public class HumidifierActuator extends StandardActuator {
    public HumidifierActuator() {
        super(DeviceClass.A2, new int[] {0, 1, 2, 3, 4});
    }

    @Override
    public void setEnvironment(Environment environment){
        if (!(this.environment == null)) {
            this.environment.removeHumidityModifier(this);
        }

        if (!(environment == null)) {
            environment.addHumidityModifier(this);
            this.environment = environment;
        }
    }

    @Override
    public double modifyEnvironmentState(Double value) {
        switch (state.getState()) {
            case 1:
                value-=40;
                break;

            case 2:
                value-=20;
                break;

            case 3:
                value+=20;
                break;

            case 4:
                value+=40;
                break;

            default:
                break;
        }

        return value;
    }
}
