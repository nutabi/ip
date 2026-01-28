package ibatun.core.tasks;

/**
 * Represents a Todo task. This is a bare implementation of a Task that does not have any additional attributes beyond
 * those defined in the Task superclass.
 * 
 * @author Binh
 * @see Task
 * @version 1.0
 */
public final class Todo extends Task {
    /**
     * Constructor for Todo.
     * 
     * @param name The name of the todo task.
     */
    public Todo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
