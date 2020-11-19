package app.arxivorg.model.userintelhandler;

import app.arxivorg.model.filters.apifilters.APIFilter;
import com.google.gson.*;

import java.lang.reflect.Type;


/*no test required for the adapter directly. They are indirectly tested with UserIntelHandler tests.
 * This class is used to allow the construction of subclasses during the json serialization and deserialization.
 * Example : using this class, APIFilter filter = new FilterByTitle("blabla") will be serialized in a way that save
 * the type of the APIFilter, to avoid a exception for trying to construct an APIFilter (APIFilter f = new APIFilter)
 * during deserialization.
 */
public class APIFilterClassAdapter implements JsonSerializer<APIFilter>, JsonDeserializer<APIFilter> {


    @Override
    public JsonElement serialize(APIFilter src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
        result.add("fields", context.serialize(src, src.getClass()));
        return result;
    }

    @Override
    public APIFilter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("fields");

        try {
            String fullName = typeOfT.getTypeName();
            String packageText = fullName.substring(0, fullName.lastIndexOf(".") + 1);

            return context.deserialize(element, Class.forName(packageText + type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("The save of your preferences failed unexpectedly");
        }
    }
}
