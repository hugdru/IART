package output;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import problem_data.Element;

import java.lang.reflect.Type;

public final class ElementSerializer implements JsonSerializer<Element> {
    @Override
    public JsonElement serialize(Element src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("name", src.getName());

        return jsonObject;
    }
}
