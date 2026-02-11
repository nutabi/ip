package ibatun.util;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ibatun.tasks.Deadline;
import ibatun.tasks.Event;
import ibatun.tasks.Task;
import ibatun.tasks.Todo;

/**
 * Adapter for serializing and deserializing Task objects with Gson.
 *
 * @author Binh
 * @version 1.0
 */
public class TaskAdapter implements JsonSerializer<Task>, JsonDeserializer<Task> {
    private final Gson defaultGson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement element = defaultGson.toJsonTree(task);
        JsonObject object = element.getAsJsonObject();

        object.addProperty("type", task.getClass().getSimpleName().toLowerCase());

        return object;
    }

    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        JsonElement type = object.get("type");

        if (type == null || type.isJsonNull()) {
            throw new JsonParseException("Missing 'type' field in Task JSON");
        }

        switch (type.getAsString()) {
        case "todo":
            return defaultGson.fromJson(json, Todo.class);
        case "deadline":
            return defaultGson.fromJson(json, Deadline.class);
        case "event":
            return defaultGson.fromJson(json, Event.class);
        default:
            throw new JsonParseException("Unknown task type: " + type.getAsString());
        }
    }
}
