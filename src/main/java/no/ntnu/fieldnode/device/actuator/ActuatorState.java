package no.ntnu.fieldnode.device.actuator;

import no.ntnu.exception.ActuatorInvalidStateException;

/**
 * A class representing the state for an actuator, allowing to switch to the valid states.
 */
public class ActuatorState {
    private final int[] states;

    private int currentState;

    /**
     * Creates a new ActuatorState.
     * Automatically sets the state to the first.
     *
     * @param states the states the actuator can be in
     */
    public ActuatorState(int[] states) {
        if (states == null || states.length == 0) {
            throw new IllegalArgumentException("Cannot create ActuatorState, because states is null or has no elements");
        }

        this.states = states;
        this.currentState = states[0];
    }

    /**
     * Creates a new ActuatorState.
     *
     * @param states the states the actuator can be in
     * @param state the current state to set
     */
    public ActuatorState(int[] states, int state) {
        if (states == null || states.length == 0) {
            throw new IllegalArgumentException("Cannot create ActuatorState, because states is null or has no elements");
        }

        this.states = states;
        setState(state);
    }

    /**
     * Sets the state.
     *
     * @param state state to set
     * @throws ActuatorInvalidStateException throws an exception if state is invalid
     */
    public void setState(int state) throws ActuatorInvalidStateException {
        if (!validState(state)) {
            throw new ActuatorInvalidStateException("Cannot set state of actuator to " + state + "because this state " +
                    "is invalid.");
        }

        currentState = state;
    }

    /**
     * Returns the current state.
     *
     * @return current state
     */
    public int getState() {
        return currentState;
    }

    private boolean validState(int state) {
        boolean valid = false;

        int searchIndex = 0;
        while (!valid) {
            if (state == states[searchIndex]) {
                valid = true;
            }
            searchIndex++;
        }

        return valid;
    }
}
