package output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import state.TaskResult;

import java.lang.reflect.Type;

public final class PartialResultSerializer implements JsonSerializer<TaskResult> {
    @Override
    public JsonElement serialize(TaskResult src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        final JsonElement jsonTimeSpan = context.serialize(src.getTimeSpan());
        jsonTimeSpan.getAsJsonObject().addProperty("duration", src.getDuration());
        jsonObject.add("timeSpan", jsonTimeSpan);

        final JsonElement jsonElements = context.serialize(src.getElements());
        jsonObject.add("elements", jsonElements);

        final JsonElement jsonTask = context.serialize(src.getTask());
        jsonObject.add("task", jsonTask);

        return jsonObject;
    }
}
