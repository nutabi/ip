package ibatun.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import ibatun.tasks.Deadline;
import ibatun.tasks.Event;
import ibatun.tasks.Task;
import ibatun.tasks.Todo;

public class TaskAdapterTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    @Test
    public void serialize_includesTypeField() {
        Task task = new Todo("read book");
        String json = gson.toJson(task, Task.class);
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        assertEquals("todo", object.get("type").getAsString());
    }

    @Test
    public void deserialize_todoRoundTrip_success() {
        Task input = new Todo("read book");
        String json = gson.toJson(input, Task.class);
        Task output = gson.fromJson(json, Task.class);
        assertInstanceOf(Todo.class, output);
        assertEquals(input.toString(), output.toString());
    }

    @Test
    public void deserialize_todoRoundTrip_preservesDoneState() {
        Task input = new Todo("read book");
        input.mark();
        String json = gson.toJson(input, Task.class);
        Task output = gson.fromJson(json, Task.class);
        assertInstanceOf(Todo.class, output);
        assertEquals(input.toString(), output.toString());
    }

    @Test
    public void deserialize_deadlineRoundTrip_success() {
        Task input = new Deadline("submit", LocalDateTime.of(2026, 2, 11, 23, 59));
        String json = gson.toJson(input, Task.class);
        Task output = gson.fromJson(json, Task.class);
        assertInstanceOf(Deadline.class, output);
        assertEquals(input.toString(), output.toString());
    }

    @Test
    public void deserialize_eventRoundTrip_success() {
        Task input = new Event("conference", LocalDateTime.of(2026, 2, 12, 9, 0), LocalDateTime.of(2026, 2, 12, 18, 0));
        String json = gson.toJson(input, Task.class);
        Task output = gson.fromJson(json, Task.class);
        assertInstanceOf(Event.class, output);
        assertEquals(input.toString(), output.toString());
    }

    @Test
    public void deserialize_missingType_throws() {
        String json = "{\"name\":\"read\",\"isDone\":false}";
        assertThrows(JsonParseException.class, () -> gson.fromJson(json, Task.class));
    }

    @Test
    public void deserialize_nullType_throws() {
        String json = "{\"type\":null,\"name\":\"read\",\"isDone\":false}";
        assertThrows(JsonParseException.class, () -> gson.fromJson(json, Task.class));
    }

    @Test
    public void deserialize_unknownType_throws() {
        String json = "{\"type\":\"mystery\",\"name\":\"read\",\"isDone\":false}";
        assertThrows(JsonParseException.class, () -> gson.fromJson(json, Task.class));
    }
}
