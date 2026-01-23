import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TaskStore {
    private final String target;
    private final List<Task> tasks;
    private final List<String> serTasks;
    private final BiConsumer<String, String[]> onRespond;

    public TaskStore(String path, BiConsumer<String, String[]> onRespond) {
        this.target = path;
        this.tasks = new ArrayList<>();
        this.serTasks = new ArrayList<>();
        this.onRespond = onRespond;
        try {
            load();
        } catch (IbatunException e) {
            onRespond.accept("Failed to load some tasks from storage", new String[] {
                    "You might want to exit and correct the file yourself.",
                    "Any actions you take might overwrite the file."
            });
        }
    }

    public Task getTask(int index) throws IndexOutOfBoundsException {
        return tasks.get(index);
    }

    public List<Task> listTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        serTasks.add(task.ser());
        save();
    }

    public void removeTask(int index) throws IndexOutOfBoundsException {
        tasks.remove(index);
        serTasks.remove(index);
        save();
    }

    public void modifyTask(int index, Consumer<Task> modifier) {
        Task task = tasks.get(index);
        modifier.accept(task);
        serTasks.set(index, task.ser());
        save();
    }

    private void load() throws IbatunException {
        Path p = Paths.get(target);
        if (!Files.exists(p)) {
            return;
        }

        try (var lines = Files.lines(p)) {
            lines.forEach(serTasks::add);
        } catch (java.io.IOException e) {
            throw new IbatunException("Failed to read from storage");
        }
        List<String> invalidTasks = new ArrayList<>();
        for (String serTask : serTasks) {
            Task task = deser(serTask);
            if (task != null) {
                tasks.add(task);
            } else {
                invalidTasks.add(serTask);
            }
        }
        if (!invalidTasks.isEmpty()) {
            onRespond.accept("Some tasks failed to load:", invalidTasks.toArray(new String[0]));
        }
    }

    private void save() {
        try {
            Files.write(Paths.get(target), serTasks);
        } catch (java.io.IOException e) {
            onRespond.accept("Failed to save tasks to storage", new String[] {
                    "Your recent changes might not be saved."
            });
        }
    }

    private static Task deser(String input) {
        if (input.isBlank()) {
            return null;
        }
        try {
            return switch (input.charAt(0)) {
                case 'T' -> Todo.deser(input);
                case 'D' -> Deadline.deser(input);
                case 'E' -> Event.deser(input);
                default -> null;
            };
        } catch (IbatunException e) {
            return null;
        }
    }
}
