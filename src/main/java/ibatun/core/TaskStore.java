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

/**
 * Handles storage and retrieval of tasks from a file.
 */
public class TaskStore {
    /**
     * The Gson instance for JSON serialization and deserialization.
     */
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    /**
     * The file path for storing tasks.
     */
    private final String path;

    /**
     * Callback to respond to the user.
     */
    private final BiConsumer<String, String[]> onRespond;

    /**
     * The list of tasks.
     */
    private List<Task> tasks;

    /**
     * Constructor for TaskStore.
     *
     * @param path      The file path for storing tasks
     * @param onRespond The callback to respond to the user
     */
    public TaskStore(String path, BiConsumer<String, String[]> onRespond) {
        this.path = path;
        this.onRespond = onRespond;
        this.tasks = new ArrayList<>();
        load();
    }

    /**
     * Gets the task at the specified index.
     *
     * @param index The index of the task
     * @return The task at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Task getTask(int index) throws IndexOutOfBoundsException {
        return tasks.get(index);
    }

    /**
     * Lists all tasks.
     *
     * @return The list of all tasks
     */
    public List<Task> listTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Adds a new task.
     *
     * @param task The task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
        save();
    }

    /**
     * Removes the task at the specified index.
     *
     * @param index The index of the task to remove
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void removeTask(int index) throws IndexOutOfBoundsException {
        tasks.remove(index);
        save();
    }

    /**
     * Modifies the task at the specified index using the provided modifier.
     *
     * @param index    The index of the task to modify
     * @param modifier The modifier function to apply to the task
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void modifyTask(int index, Consumer<Task> modifier) throws IndexOutOfBoundsException {
        Task task = tasks.get(index);
        modifier.accept(task);
        save();
    }

    /**
     * Loads tasks from the storage file.
     */
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

    /**
     * Saves tasks to the storage file.
     */
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
