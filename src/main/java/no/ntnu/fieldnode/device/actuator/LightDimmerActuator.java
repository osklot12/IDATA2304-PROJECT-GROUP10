package no.ntnu.fieldnode.device.actuator;

import no.ntnu.environment.Environment;
import no.ntnu.fieldnode.device.DeviceClass;

/**
 * An actuator controlling a light dimmer.
 */
public class LightDimmerActuator extends StandardActuator {
    /**
     * Creates a new LightDimmerActuator.
     */
    public LightDimmerActuator() {
        super(DeviceClass.A3, new int[] {0, 1, 2, 3, 4, 5, 6});
    }

    @Override
    public void setEnvironment(Environment environment){
        if (this.environment != null) {
            this.environment.removeLuminosityModifier(this);
        }

        if (environment != null) {
            environment.addLuminosityModifier(this);
            this.environment = environment;
        }
    }

    @Override
    public double modifyEnvironmentState(Double value) {
        switch (state.getState()) {
            case 1:
                value-=1500;
                break;

            case 2:
                value-=1000;
                break;

            case 3:
                value-=500;
                break;

            case 4:
                value+=500;
                break;

            case 5:
                value+=1000;
                break;

            case 6:
                value+=1500;
                break;

            default:
                break;
        }

        return value;
    }
}
