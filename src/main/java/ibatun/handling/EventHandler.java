package ibatun.handling;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import ibatun.storage.TaskStore;
import ibatun.tasks.Event;
import ibatun.tasks.Task;
import ibatun.errors.IbatunException;
import ibatun.util.ArgTools;
import ibatun.util.DatetimeConverter;

/**
 * A handler for creating {@code Event} tasks.
 *
 * @see Event
 */
final class EventHandler extends Handler {
    /**
     * Constructor for EventHandler.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    EventHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "event".equals(command);
    }

    @Override
    void handle(String[] args) {
        String[] parts = ArgTools.splitByDelimiters(args, "/from", "/to");

        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
            fail("The event command requires a description, start date/time, and end date/time.\n\n"
                    + "Format: event <description> /from <start date/time> /to <end date/time>");
            return;
        }

        LocalDateTime from;
        LocalDateTime to;
        try {
            from = DatetimeConverter.parse(parts[1]);
            to = DatetimeConverter.parse(parts[2]);
        } catch (IbatunException e) {
            fail(e.getMessage());
            return;
        }

        Task newTask = new Event(parts[0], from, to);
        try {
            store.add(newTask);
        } catch (IbatunException e) {
            fail(e.getMessage());
            return;
        }
        succeed("Got it. I've added this event: " + newTask.toString());
        return;
    }
}
