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

public class TaskStore {
    private final String path;
    private final BiConsumer<String, String[]> onRespond;
    private final ObjectMapper mapper;

    private List<Task> tasks;

    public TaskStore(String path, BiConsumer<String, String[]> onRespond) {
        this.path = path;
        this.onRespond = onRespond;
        this.mapper = new ObjectMapper();
        load();

    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        return tasks.get(index);
    }

    public List<Task> listTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(int index) throws IndexOutOfBoundsException {
        tasks.remove(index);
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
