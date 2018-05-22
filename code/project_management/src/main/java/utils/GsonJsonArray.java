package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.function.Function;

public final class GsonJsonArray {

    private GsonJsonArray() {
    }

    public static <T> ArrayList<T> toArrayList(JsonArray jsonArray, Function<JsonElement, T> AsType) {
        final ArrayList<T> arrayList = new ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            arrayList.add(AsType.apply(jsonElement));
        }
        return arrayList;
    }
}
