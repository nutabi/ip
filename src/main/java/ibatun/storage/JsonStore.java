package ibatun.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ibatun.errors.IbatunException;
import ibatun.errors.IbatunIOException;
import ibatun.tasks.Task;
import ibatun.util.DatetimeAdapter;
import ibatun.util.TaskAdapter;

/**
 * Handles storage and retrieval of data in JSON format.
 */
public final class JsonStore extends TaskStore {
    /**
     * The Gson instance for JSON serialisation and deserialisation.
     */
    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    private final String targetPath;

    /**
     * Constructor for JsonStore.
     *
     * @param targetPath The file path for storing data
     * @param onRespond  The callback to respond to the user
     * @throws IbatunException if there is an error loading data
     */
    public JsonStore(String targetPath) throws IbatunException {
        super(loadTasks(targetPath));
        this.targetPath = targetPath;
    }

    @Override
    public void add(Task task) throws IbatunException {
        super.add(task);
        dumpTasks();
    }

    @Override
    public void remove(int index) throws IbatunException {
        super.remove(index);
        dumpTasks();
    }

    @Override
    public void modify(int index, Consumer<Task> modifier) throws IbatunException {
        super.modify(index, modifier);
        dumpTasks();
    }

    private static List<Task> loadTasks(String targetPath) throws IbatunException {
        List<Task> tasks = new ArrayList<>();
        // Load existing data
        try {
            Path filePath = Paths.get(targetPath);
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
            throw new IbatunIOException("Failed to load data");
        }
        return tasks;
    }

    private void dumpTasks() throws IbatunException {
        try {
            Path filePath = Paths.get(targetPath);
            String json = gson.toJson(tasks);
            Files.writeString(filePath, json);
        } catch (InvalidPathException | IOException e) {
            throw new IbatunIOException("Failed to save data");
        }
    }
}
