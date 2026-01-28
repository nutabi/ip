package ibatun.core.tasks;

import java.time.LocalDateTime;

import ibatun.util.DatetimeConverter;

/**
 * Represents an event task with a start and end time.
 *
 * @author Binh
 * @version 1.0
 * @see Task
 */
public final class Event extends Task {
    /**
     * The start time of the event.
     */
    private LocalDateTime from;

    /**
     * The end time of the event.
     */
    private LocalDateTime to;

    /**
     * Creates an Event task.
     *
     * @param name The name of the event
     * @param from The start time of the event
     * @param to   The end time of the event
     * @see DatetimeConverter DatetimeConverter for formatting {@code from} and {@code to}
     */
    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the start time of the event.
     *
     * @return The start time of the event
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Gets the end time of the event.
     *
     * @return The end time of the event
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String
                .format("[E]%s (from: %s to: %s)", super.toString(), DatetimeConverter.format(from),
                        DatetimeConverter.format(to));
    }
}
