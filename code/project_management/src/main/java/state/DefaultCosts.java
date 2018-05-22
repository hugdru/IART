package state;

import algorithm.Costs;

public final class DefaultCosts implements Costs {
    @Override
    public double step(State sourceState, State destinationState) {
        return 0;
    }

    @Override
    public double heuristic(State state) {
        return 0;
    }
}
