package ibatun.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ibatun.core.tasks.Task;

/**
 * Handles storage and retrieval of tasks from a file.
 */
public class TaskStore {
    /**
     * The file path for storing tasks.
     */
    private final String path;

    /**
     * Callback to respond to the user.
     */
    private final BiConsumer<String, String[]> onRespond;

    /**
     * The JSON object mapper.
     */
    private final ObjectMapper mapper;

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
        this.mapper = new ObjectMapper();
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
        return tasks;
    }

    /**
     * Adds a new task.
     *
     * @param task The task to add
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the specified index.
     *
     * @param index The index of the task to remove
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void removeTask(int index) throws IndexOutOfBoundsException {
        tasks.remove(index);
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
                    tasks = mapper.readValue(content, new TypeReference<List<Task>>() {
                    });
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
            String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks);
            Files.writeString(filePath, content);
        } catch (InvalidPathException | IOException e) {
            onRespond
                    .accept("Error: Failed to save tasks to storage.",
                            new String[] { "Make sure the file path is valid and writable." });
        }
    }
}
