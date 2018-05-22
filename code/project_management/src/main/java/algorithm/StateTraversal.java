package algorithm;

import com.google.common.collect.ImmutableList;
import org.graphstream.algorithm.util.FibonacciHeap;
import state.State;

import java.util.HashMap;
import java.util.HashSet;

public final class StateTraversal {

    private StateTraversal() {
    }

    public static ImmutableList<State> compute(State initialState, Costs costs) {

        HashSet<State> closedSet = new HashSet<>();

        HashMap<State, State> optimumPath = new HashMap<>();

        HashMap<State, Double> gScore = new HashMap<>();
        gScore.put(initialState, 0.0);

        HashMap<State, FibonacciHeap<Double, State>.Node> fibonacciNodeTracker = new HashMap<>();
        FibonacciHeap<Double, State> heap = new FibonacciHeap<>();
        fibonacciNodeTracker.put(initialState, heap.add(costs.heuristic(initialState), initialState));

        while (heap.size() != 0) {
            State currentState = heap.extractMin();

            if (currentState.isFinalState()) {
                return buildPath(optimumPath, currentState);
            }

            closedSet.add(currentState);

            for (State generatedState : currentState.generateSuccessors()) {
                if (closedSet.contains(generatedState)) {
                    continue;
                }

                FibonacciHeap<Double, State>.Node generatedStateFibonacciNode = fibonacciNodeTracker.get(generatedState);

                if (generatedStateFibonacciNode == null) {
                    addNewStateToHeap(currentState, generatedState, fibonacciNodeTracker, heap, costs, gScore);
                } else {
                    Double currentGeneratedStateGScore = gScore.get(generatedState);
                    double tentativeGeneratedStateGScore = costs.g(currentState, generatedState, gScore.get(currentState));

                    if (tentativeGeneratedStateGScore >= currentGeneratedStateGScore) {
                        continue;
                    }
                    gScore.put(generatedState, tentativeGeneratedStateGScore);
                    heap.decreaseKey(generatedStateFibonacciNode, tentativeGeneratedStateGScore + costs.heuristic(generatedState));
                }
                optimumPath.put(generatedState, currentState);
            }
        }

        return null;
    }

    private static void addNewStateToHeap(State currentState, State generatedState, HashMap<State, FibonacciHeap<Double, State>.Node> fibonacciNodeTracker, FibonacciHeap<Double, State> heap, Costs costs, HashMap<State, Double> gScore) {
        double generatedStateGScore = costs.g(currentState, generatedState, gScore.get(currentState));
        gScore.put(generatedState, generatedStateGScore);
        double generateStateFScore = costs.f(generatedState, generatedStateGScore);
        fibonacciNodeTracker.put(generatedState, heap.add(generateStateFScore, generatedState));
    }

    private static ImmutableList<State> buildPath(HashMap<State, State> optimumPath, State currentState) {
        ImmutableList.Builder<State> pathBuilder = new ImmutableList.Builder<>();
        pathBuilder.add(currentState);
        while ((currentState = optimumPath.get(currentState)) != null) {
            pathBuilder.add(currentState);
        }
        return pathBuilder.build();
    }
}
