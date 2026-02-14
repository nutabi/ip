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
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import ibatun.errors.IbatunCorruptedDataException;
import ibatun.errors.IbatunException;
import ibatun.errors.IbatunFileException;
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
        super(loadTasks(targetPath, false));
        assert targetPath != null : "Target path cannot be null";
        assert !targetPath.isBlank() : "Target path cannot be blank";
        this.targetPath = targetPath;
    }

    /**
     * Constructor for JsonStore that can recover from corrupted data.
     *
     * @param targetPath         The file path for storing data
     * @param recoverCorruptData Whether to proceed with empty data if parsing fails
     * @throws IbatunException if there is an error loading data
     */
    public JsonStore(String targetPath, boolean recoverCorruptData) throws IbatunException {
        super(loadTasks(targetPath, recoverCorruptData));
        assert targetPath != null : "Target path cannot be null";
        assert !targetPath.isBlank() : "Target path cannot be blank";
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

    private static List<Task> loadTasks(String targetPath, boolean recoverCorruptData) throws IbatunException {
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
        } catch (DateTimeParseException | JsonParseException e) {
            if (recoverCorruptData) {
                return tasks;
            }
            throw new IbatunCorruptedDataException(
                    "Stored data looks corrupted. If you continue, existing data will be overwritten.");
        } catch (InvalidPathException | IOException e) {
            throw new IbatunFileException("Failed to load data");
        }
        return tasks;
    }

    private void dumpTasks() throws IbatunException {
        try {
            Path filePath = Paths.get(targetPath);
            Path parentDir = filePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            String json = gson.toJson(tasks);
            Files.writeString(filePath, json);
        } catch (InvalidPathException | IOException e) {
            throw new IbatunFileException("Failed to save data");
        }
    }
}
