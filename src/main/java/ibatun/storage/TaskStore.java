package ibatun.storage;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import ibatun.errors.IbatunException;
import ibatun.errors.IbatunTaskNotFoundException;
import ibatun.tasks.Task;

/**
 * Abstract class representing a storage for tasks.
 */
public abstract class TaskStore {
    protected final List<Task> tasks;

    protected TaskStore(List<Task> tasks) {
        assert tasks != null : "Tasks list cannot be null";

        this.tasks = tasks;
    }

    /**
     * Get a task by its index.
     *
     * @param index The index of the task
     * @return The task at the specified index
     * @throws IbatunException if the task is not found
     */
    public Task get(int index) throws IbatunException {
        if (index < 0 || index >= tasks.size()) {
            throw new IbatunTaskNotFoundException();
        }
        return tasks.get(index);
    }

    /**
     * List all tasks.
     *
     * @return A read-only list of all tasks
     * @throws IbatunException if there is an error retrieving the list
     */
    public List<Task> list() throws IbatunException {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Add a new task.
     *
     * @param task The task to add
     * @throws IbatunException if there is an error adding the task
     */
    public void add(Task task) throws IbatunException {
        assert task != null : "Task to add cannot be null";
        tasks.add(task);
    }

    /**
     * Remove a task by its index.
     *
     * @param index The index of the task to remove
     * @throws IbatunException if there is an error removing the task
     */
    public void remove(int index) throws IbatunException {
        if (index < 0 || index >= tasks.size()) {
            throw new IbatunTaskNotFoundException();
        }
        tasks.remove(index);
    }

    /**
     * Modify a task by its index using the provided modifier.
     *
     * @param index    The index of the task to modify
     * @param modifier The modifier to apply to the task
     * @throws IbatunException if there is an error modifying the task
     */
    public void modify(int index, Consumer<Task> modifier) throws IbatunException {
        assert modifier != null : "Modifier cannot be null";
        if (index < 0 || index >= tasks.size()) {
            throw new IbatunTaskNotFoundException();
        }
        Task task = tasks.get(index);
        modifier.accept(task);
    }
}
