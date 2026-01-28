package ibatun.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ibatun.core.tasks.Task;
import ibatun.util.DatetimeAdapter;
import ibatun.util.TaskAdapter;

public class TaskStore {
    private final String path;
    private final BiConsumer<String, String[]> onRespond;
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    private List<Task> tasks;

    public TaskStore(String path, BiConsumer<String, String[]> onRespond) {
        this.path = path;
        this.onRespond = onRespond;
        this.tasks = new ArrayList<>();
        load();
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        return tasks.get(index);
    }

    public List<Task> listTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addTask(Task task) {
        tasks.add(task);
        save();
    }

    public void removeTask(int index) throws IndexOutOfBoundsException {
        tasks.remove(index);
        save();
    }

    public void modifyTask(int index, Consumer<Task> modifier) {
        Task task = tasks.get(index);
        modifier.accept(task);
        save();
    }

    private void load() {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                if (!content.isBlank()) {
                    List<Task> loaded = gson.fromJson(content, new TypeToken<List<Task>>() {
                    }.getType());
                    if (loaded != null) {
                        tasks = loaded;
                    }
                }
            }
        } catch (InvalidPathException | IOException | DateTimeParseException e) {
            onRespond
                    .accept("Error: Failed to load tasks from storage.",
                            new String[] { "Make sure the file path is valid and readable.",
                                "If you decide to continue, the storage might be overwritten." });
        }
    }

    private void save() {
        try {
            Path filePath = Paths.get(path);
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            String json = gson.toJson(tasks);
            Files.writeString(filePath, json);
        } catch (InvalidPathException | IOException e) {
            onRespond
                    .accept("Error: Failed to save tasks to storage.",
                            new String[] { "Make sure the file path is valid and writable." });
        }
    }
}
