import algorithm.Costs;
import algorithm.StateTraversal;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import output.ResultOutput;
import parser.Parser;
import problem_data.ProblemData;
import state.AStarCosts;
import state.BranchAndBoundCosts;
import state.DefaultCosts;
import state.State;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Expected just one argument, the database filepath.");
            return;
        }

        String inputFilePath = args[0];

        Parser parser = new Parser();

        try {
            ProblemData problemData = parser.parse(inputFilePath);
            saveResult(State.createInitialState(problemData), "default", new DefaultCosts(), inputFilePath);
            saveResult(State.createInitialState(problemData), "branchbound", new BranchAndBoundCosts(), inputFilePath);
            saveResult(State.createInitialState(problemData), "astar", new AStarCosts(), inputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveResult(State initialState, String filenameSuffix, Costs costs, String inputFilePath) throws IOException {
        long startTime = System.nanoTime();
        ImmutableList<State> path = StateTraversal.compute(initialState, costs);
        long estimatedTime = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);

        Preconditions.checkArgument(path != null && path.size() != 0, "Could not reach a final state (" + filenameSuffix + ")");

        System.err.println(filenameSuffix + ": " + estimatedTime);

        String outputFilePath = getOutputFilePath(inputFilePath, filenameSuffix);
        ResultOutput.toFile(path.get(0).getResult(), outputFilePath);
    }

    private static String getOutputFilePath(String str, String filenameSuffix) {
        if (str.contains(".")) {
            str = str.substring(0, str.lastIndexOf('.'));
        }
        return str + "_" + filenameSuffix + ".json";
    }
}
