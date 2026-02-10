package ibatun.handling;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import ibatun.core.TaskStore;
import ibatun.core.tasks.Deadline;
import ibatun.core.tasks.Task;
import ibatun.errors.IbatunException;
import ibatun.util.ArgTools;
import ibatun.util.DatetimeConverter;

/**
 * A handler for creating {@code Deadline} tasks.
 *
 * @see Deadline
 */
final class DeadlineHandler extends Handler {
    DeadlineHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "deadline".equals(command);
    }

    @Override
    void handle(String[] args) {
        String[] parts = ArgTools.splitByDelimiters(args, "/by");

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            fail("The deadline command requires a description and a due date/time.\n\n"
                    + "Format: deadline <description> /by <due date/time>");
            return;
        }

        LocalDateTime by;
        try {
            by = DatetimeConverter.parse(parts[1]);
        } catch (IbatunException e) {
            fail(e.getMessage());
            return;
        }

        Task newTask = new Deadline(parts[0], by);
        store.addTask(newTask);
        succeed("Got it. I've added this deadline: " + newTask.toString());
        return;
    }

}
