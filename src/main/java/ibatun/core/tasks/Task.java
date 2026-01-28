package ibatun.core.tasks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a task with a name and completion status.
 * 
 * @author Binh
 * @version 1.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Todo.class, name = "todo"),
    @JsonSubTypes.Type(value = Deadline.class, name = "deadline"),
    @JsonSubTypes.Type(value = Event.class, name = "event") })
public abstract sealed class Task permits Todo, Deadline, Event {
    /**
     * The name of the task.
     */
    protected String name;

    /**
     * Indicates whether the task is done.
     */
    protected boolean isDone;

    /**
     * Constructor for Task.
     * 
     * @param name The name of the task.
     */
    protected Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns whether the task is marked done.
     * 
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Unmarks the task as done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Gets the name of the task.
     * 
     * @return The name of the task.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", (isDone ? "[X]" : "[ ]"), name);
    }
}
