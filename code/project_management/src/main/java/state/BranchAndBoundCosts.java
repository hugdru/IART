package state;

import algorithm.Costs;

public final class BranchAndBoundCosts implements Costs {
    @Override
    public double step(State sourceState, State destinationState) {
        return sourceState.getTotalTimeDelta(destinationState);
    }

    @Override
    public double heuristic(State state) {
        return 0;
    }
}
