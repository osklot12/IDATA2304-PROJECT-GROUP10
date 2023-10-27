package no.ntnu.environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An environmental state described by a Double value.
 */
public class EnvironmentState {
    private final double initialValue;
    private final double allowedRange;
    private final List<EnvironmentStateModifier> modifiers;

    /**
     * Creates an EnvironmentState.
     *
     * @param initialValue the initial value of the state
     * @param allowedRange the range of modification allowed
     */
    public EnvironmentState(double initialValue, double allowedRange) {
        this.initialValue = initialValue;
        this.allowedRange = allowedRange;
        this.modifiers = new ArrayList<>();
    }

    /**
     * Adds a modifier to the state.
     *
     * @param modifier the modifier to add
     * @return true if successfully added
     */
    public boolean addModifier(EnvironmentStateModifier modifier) {
        return modifiers.add(modifier);
    }

    /**
     * Removes a modifier from the state.
     *
     * @param modifier modifier to remove
     * @return true if successfully removed
     */
    public boolean removeModifier(EnvironmentStateModifier modifier) {
        return modifiers.remove(modifier);
    }

    /**
     * Returns the modified value of the state.
     *
     * @return state value modified by modifiers
     */
    public double getModifiedValue() {
        double value = iterateStateModifiers(initialValue, modifiers.iterator());

        if (value > initialValue + (allowedRange / 2)) {
            value = initialValue + (allowedRange / 2);
        }

        if (value < initialValue - (allowedRange - 2)) {
            value = initialValue - (allowedRange / 2);
        }

        return value;
    }

    private double iterateStateModifiers(double value, Iterator<EnvironmentStateModifier> it) {
        if (!it.hasNext()) {
            return value;
        }

        return iterateStateModifiers(it.next().modifyEnvironmentState(value), it);
    }
}
