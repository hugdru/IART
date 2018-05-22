package output;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import problem_data.Element;
import problem_data.Task;
import state.TaskResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class ResultOutput {

    private static Gson gson;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Element.class, new ElementSerializer());
        gsonBuilder.registerTypeAdapter(TaskResult.class, new PartialResultSerializer());
        gsonBuilder.registerTypeAdapter(Range.class, new RangeDoubleSerializer());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer());
        gsonBuilder.setPrettyPrinting();
        ResultOutput.gson = gsonBuilder.create();
    }

    private ResultOutput() {
    }

    public static String toJson(ImmutableList<TaskResult> result) {
        return gson.toJson(result);
    }

    public static void toFile(ImmutableList<TaskResult> result, String filepath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        writer.write(toJson(result));
        writer.close();
    }
}
