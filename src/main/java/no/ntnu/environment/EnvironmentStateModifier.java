package no.ntnu.environment;

/**
 * An object modifying an environmental state in some way.
 */
public interface EnvironmentStateModifier {
    /**
     * Modifies an environmental state represented with a Double value.
     *
     * @param value the environmental state value to modify
     * @return the modified environmental state
     */
    double modifyEnvironmentState(Double value);
}
