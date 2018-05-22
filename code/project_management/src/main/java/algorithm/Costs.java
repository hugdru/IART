package algorithm;

import state.State;

public interface Costs {

    double step(State sourceState, State destinationState);

    double heuristic(State state);

    default double g(State currentState, State generatedState, double currentStateGScore) {
        return currentStateGScore + step(currentState, generatedState);
    }

    default double f(State generatedState, double generatedStateGScore) {
        return generatedStateGScore + heuristic(generatedState);
    }

    default double f(State currentState, State generatedState, double currentStateGScore) {
        return g(currentState, generatedState, currentStateGScore) + heuristic(generatedState);
    }
}
