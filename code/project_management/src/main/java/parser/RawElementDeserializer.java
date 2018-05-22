package parser;

import com.google.gson.*;

import java.lang.reflect.Type;

final class RawElementDeserializer implements JsonDeserializer<RawElement> {
    @Override
    public RawElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray skillsJsonArray = jsonObject.get("skills").getAsJsonArray();
        int[][] skills = new int[skillsJsonArray.size()][];

        int i = 0;
        for (JsonElement jsonElement : skillsJsonArray) {
            JsonArray skillJsonArray = jsonElement.getAsJsonArray();
            int skillName = skillJsonArray.get(0).getAsInt();
            int skillValue = skillJsonArray.get(1).getAsInt();
            skills[i] = new int[]{skillName, skillValue};
            ++i;
        }

        return new RawElement(name, skills);
    }
}
