package parser;

import com.google.gson.*;
import utils.GsonJsonArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

final class RawTaskDeserializer implements JsonDeserializer<RawTask> {

    @Override
    public RawTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        double duration = jsonObject.get("duration").getAsDouble();

        JsonArray precedencesJsonArray = jsonObject.get("precedences").getAsJsonArray();
        ArrayList<Integer> precedences = GsonJsonArray.toArrayList(precedencesJsonArray, JsonElement::getAsInt);

        JsonArray skillsJsonArray = jsonObject.get("skills").getAsJsonArray();
        ArrayList<Integer> skills = GsonJsonArray.toArrayList(skillsJsonArray, JsonElement::getAsInt);

        return new RawTask(name, duration, precedences, skills);
    }
}