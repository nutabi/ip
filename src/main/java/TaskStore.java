import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskStore {
    private final String target;
    private final List<Task> tasks;
    private final List<String> serTasks;

    public TaskStore(String path) {
        this.target = path;
        this.tasks = new ArrayList<>();
        this.serTasks = new ArrayList<>();
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

    private void load() {
        // Open file to read
        // Load the content into serTasks
        // Iterate over serTasks to deser them into Task
    }

    private void save() {
        // Open file to overwrite
        // Dump the content of serTasks
    }
}
