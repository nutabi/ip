package ibatun.core.tasks;

import java.time.LocalDateTime;

import ibatun.util.DatetimeConverter;

/**
 * Represents a deadline task with a specific due date and time.
 * 
 * @author Binh
 * @see Task
 * @version 1.0
 */
public final class Deadline extends Task {
    /**
     * The due date and time of the deadline.
     */
    private LocalDateTime by;

    /**
     * Creates a Deadline task.
     * 
     * @param name The name of the deadline task
     * @param by   The due date and time of the deadline
     * @see DatetimeConverter DatetimeConverter for formatting {@code by}
     */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    /**
     * Gets the due date and time of the deadline.
     * 
     * @return The due date and time of the deadline
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), DatetimeConverter.format(by));
    }
}
