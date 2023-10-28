package no.ntnu.environment;

/**
 * A class simulating an actual farming environment.
 */
public class Environment {
    private final EnvironmentState temperature;
    private final EnvironmentState humidity;
    private final EnvironmentState luminosity;

    /**
     * Creates a new Environment.
     */
    public Environment() {
        this.temperature = new EnvironmentState(27, 10);
        this.humidity = new EnvironmentState(50, 80);
        this.luminosity = new EnvironmentState(10000, 5000);
    }

    /**
     * Returns the simulated temperature of the environment.
     *
     * @return simulated temperature
     */
    public double getSimulatedTemperature() {
        return temperature.getModifiedValue();
    }

    /**
     * Adds a modifier to the temperature of the environment.
     *
     * @param modifier modifier to add
     * @return true if successfully added
     */
    public boolean addTemperatureModifier(EnvironmentStateModifier modifier) {
        return temperature.addModifier(modifier);
    }

    /**
     * Removes a modifier from the temperature of the environment.
     *
     * @param modifier modifier to remove
     * @return true if successfully removed
     */
    public boolean removeTemperatureModifier(EnvironmentStateModifier modifier) {
        return temperature.removeModifier(modifier);
    }

    /**
     * Returns the simulated humidity of the environment.
     *
     * @return simulated humidity
     */
    public double getSimulatedHumidity() {
        return humidity.getModifiedValue();
    }

    /**
     * Adds a modifier to the humidity of the environment.
     *
     * @param modifier modifier to add
     * @return true if successfully added
     */
    public boolean addHumidityModifier(EnvironmentStateModifier modifier) {
        return humidity.addModifier(modifier);
    }

    /**
     * Removes a modifier from the humidity of the environment.
     *
     * @param modifier modifier to remove
     * @return true if successfully removed
     */
    public boolean removeHumidityModifier(EnvironmentStateModifier modifier) {
        return humidity.removeModifier(modifier);
    }

    /**
     * Returns the simulated luminosity of the environment.
     *
     * @return simulated luminosity
     */
    public double getSimulatedLuminosity() {
        return luminosity.getModifiedValue();
    }

    /**
     * Adds a modifier to the luminosity of the environment.
     *
     * @param modifier modifier to add
     * @return true if successfully added
     */
    public boolean addLuminosityModifier(EnvironmentStateModifier modifier) {
        return luminosity.addModifier(modifier);
    }

    /**
     * Removes a modifier from the luminosity of the environment.
     *
     * @param modifier modifier to remove
     * @return true if successfully removed
     */
    public boolean removeLuminosityModifier(EnvironmentStateModifier modifier) {
        return luminosity.removeModifier(modifier);
    }
}
