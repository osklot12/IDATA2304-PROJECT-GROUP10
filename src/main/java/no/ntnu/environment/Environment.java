package no.ntnu.environment;

import java.util.List;

/**
 * A class simulating an actual farming environment.
 */
public class Environment {
    private int temperature;

    private int humidity;

    private int luminosity;

    private List<EnvironmentModifier> modifiers;

    /**
     * Creates a new Environment.
     */
    public Environment() {
        this.temperature = 27;
        this.humidity = 50;
        this.luminosity = 20000;
    }

    /**
     * Returns the simulated temperature of the environment.
     *
     * @return simulated temperature
     */
    public int getSimulatedTemperature() {
        return temperature;
    }
}
