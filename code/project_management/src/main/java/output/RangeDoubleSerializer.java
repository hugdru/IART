package output;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public final class RangeDoubleSerializer implements JsonSerializer<Range<Double>> {
    @Override
    public JsonElement serialize(Range<Double> src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("start", src.lowerEndpoint());
        jsonObject.addProperty("end", src.upperEndpoint());

        return jsonObject;
    }
}
