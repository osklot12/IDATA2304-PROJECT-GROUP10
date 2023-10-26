package no.ntnu.environment;

import no.ntnu.greenhouse.Actuator;

/**
 * An abstract class representing a modifier for an environment.
 * The modifier changes some state for an environment.
 */
public abstract class EnvironmentModifier {
    private final Actuator modifyingActuator;

    /**
     * Constructor for the EnvironmentModifier class.
     *
     * @param modifyingActuator the actuator modifying the environment
     */
    public EnvironmentModifier(Actuator modifyingActuator) {
        this.modifyingActuator = modifyingActuator;
    }

    /**
     * Returns the actuator responsible for modifying the environment.
     *
     * @return the modifying actuator
     */
    public Actuator getModifyingActuator() {
        return modifyingActuator;
    }

    /**
     * Modifies some state for an environment.
     */
    abstract void modify(Environment environment);
}
