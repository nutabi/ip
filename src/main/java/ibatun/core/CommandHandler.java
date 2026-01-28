package ibatun.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import ibatun.core.tasks.Deadline;
import ibatun.core.tasks.Event;
import ibatun.core.tasks.Task;
import ibatun.core.tasks.Todo;
import ibatun.errors.IbatunException;
import ibatun.util.DatetimeConverter;

/**
 * Handles user commands and interacts with the task storage.
 */
public class CommandHandler {
    /**
     * Error message for empty todo description.
     */
    private static final String TODO_ERROR = "The description of a todo cannot be empty.";

    /**
     * Error message for malformed deadline command.
     */
    private static final String DEADLINE_ERROR = "Deadline must have a /by clause.";

    /**
     * Error message for malformed event command.
     */
    private static final String EVENT_ERROR = "Event must have /from and /to clauses.";

    /**
     * Error message for invalid task number.
     */
    private static final String TASK_NUM_ERROR = "Please provide a valid task number.";

    /**
     * Mapping of command strings to their handler functions.
     */
    private final Map<String, Function<String[], String>> commands;

    /**
     * Callback to respond to the user.
     */
    private final BiConsumer<String, String[]> onRespond;

    /**
     * The task storage.
     */
    private final TaskStore store;

    /**
     * Constructor for CommandHandler.
     * 
     * @param onRespond The callback to respond to the user
     * @param store     The task storage
     */
    public CommandHandler(BiConsumer<String, String[]> onRespond, TaskStore store) {
        this.commands = Map
                .of("todo", this::handleTodo, "deadline", this::handleDeadline, "event", this::handleEvent, "list",
                        this::handleList, "mark", this::handleMark, "unmark", this::handleUnmark, "delete",
                        this::handleDelete);
        this.onRespond = onRespond;
        this.store = store;
    }

    /**
     * Handles a user command.
     * 
     * @param input The user input split into command and arguments
     * @return true if the application should continue running, false if it should exit
     * @throws IbatunException if there is an error processing the command
     */
    public boolean handle(String[] input) throws IbatunException {
        String command = input[0].toLowerCase();

        // Check exit command
        if (command.equals("bye")) {
            return false;
        }

        // Dispatch to appropriate handler
        Function<String[], String> action = commands.get(command);
        if (action != null) {
            String err = action.apply(java.util.Arrays.copyOfRange(input, 1, input.length));
            if (err != null) {
                throw new IbatunException(err);
            }
        } else {
            onRespond.accept("I don't get what you mean :(", new String[0]);
        }
        return true;
    }

    /**
     * Handles the 'todo' command.
     * 
     * @param args The arguments for the todo command
     * @return Error message if any, null otherwise
     */
    private String handleTodo(String[] args) {
        String description = String.join(" ", args);
        if (description.isBlank()) {
            return TODO_ERROR;
        }

        Task newTask = new Todo(description);
        store.addTask(newTask);
        onRespond.accept("Got it. I've added this todo:", new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    /**
     * Handles the 'deadline' command.
     * 
     * @param args The arguments for the deadline command
     * @return Error message if any, null otherwise
     */
    private String handleDeadline(String[] args) {
        String[] parts = splitByDelimiter(args, "/by");

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            return DEADLINE_ERROR;
        }

        LocalDateTime by;
        try {
            by = DatetimeConverter.parse(parts[1]);
        } catch (IbatunException e) {
            return e.getMessage();
        }

        Task newTask = new Deadline(parts[0], by);
        store.addTask(newTask);
        onRespond.accept("Got it. I've added this deadline:", new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    /**
     * Handles the 'event' command.
     * 
     * @param args The arguments for the event command
     * @return Error message if any, null otherwise
     */
    private String handleEvent(String[] args) {
        String[] parts = splitByDelimiter(args, "/from", "/to");

        // Expect exactly 3 parts: description, start time, end time
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
            return EVENT_ERROR;
        }

        LocalDateTime from;
        LocalDateTime to;
        try {
            from = DatetimeConverter.parse(parts[1]);
            to = DatetimeConverter.parse(parts[2]);
        } catch (IbatunException e) {
            return e.getMessage();
        }
        Task newTask = new Event(parts[0], from, to);
        store.addTask(newTask);
        onRespond.accept("Got it. I've added this event:", new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    /**
     * Handles the 'list' command.
     * 
     * @param args The arguments for the list command
     * @return Error message if any, null otherwise
     */
    private String handleList(String[] args) {
        List<Task> tasks = store.listTasks();
        if (tasks.isEmpty()) {
            onRespond.accept("You ain't got no task.", new String[0]);
        } else {
            List<String> indexedTasks = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                indexedTasks.add(String.format("%d. %s", i + 1, tasks.get(i)));
            }
            onRespond.accept("Here are your tasks:", indexedTasks.toArray(new String[0]));
        }
        return null;
    }

    /**
     * Handles the 'mark' command.
     * 
     * @param args The arguments for the mark command
     * @return Error message if any, null otherwise
     */
    private String handleMark(String[] args) {
        int idx;
        try {
            idx = Integer.parseInt(args[0]) - 1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return TASK_NUM_ERROR;
        }
        if (idx < 0 || idx >= store.listTasks().size()) {
            return TASK_NUM_ERROR;
        }

        Task t = store.getTask(idx);
        if (t.isDone()) {
            onRespond.accept("This task is already marked done!", new String[] { t.toString() });
            return null;
        }
        store.modifyTask(idx, Task::mark);
        onRespond.accept("Bravo! You did it :D", new String[] { t.toString() });
        return null;
    }

    /**
     * Handles the 'unmark' command.
     * 
     * @param args The arguments for the unmark command
     * @return Error message if any, null otherwise
     */
    private String handleUnmark(String[] args) {
        int idx;
        try {
            idx = Integer.parseInt(args[0]) - 1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return TASK_NUM_ERROR;
        }
        if (idx < 0 || idx >= store.listTasks().size()) {
            return TASK_NUM_ERROR;
        }

        Task t = store.getTask(idx);
        if (!t.isDone()) {
            onRespond.accept("This task is already unmarked!", new String[] { t.toString() });
            return null;
        }
        store.modifyTask(idx, Task::unmark);
        onRespond.accept("Bomb you, why never do properly?", new String[] { t.toString() });
        return null;
    }

    /**
     * Handles the 'delete' command.
     * 
     * @param args The arguments for the delete command
     * @return Error message if any, null otherwise
     */
    private String handleDelete(String[] args) {
        int idx;
        try {
            idx = Integer.parseInt(args[0]) - 1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return TASK_NUM_ERROR;
        }
        if (idx < 0 || idx >= store.listTasks().size()) {
            return TASK_NUM_ERROR;
        }

        Task t = store.getTask(idx);
        store.removeTask(idx);
        onRespond.accept("Aight, I nuked it!", new String[] { t.toString(), getTaskCountMsg() });
        return null;
    }

    /**
     * Gets a message indicating the current task count.
     * 
     * @return The task count message
     */
    private String getTaskCountMsg() {
        return String.format("You have %d tasks in total now.", store.listTasks().size());
    }

    /**
     * Splits the arguments by the specified delimiters.
     * 
     * @param args       The arguments to split
     * @param delimiters The delimiters to split by
     * @return The split segments
     */
    private String[] splitByDelimiter(String[] args, String... delimiters) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int delimiterIndex = 0;

        for (String arg : args) {
            // Check if this arg is the next expected delimiter
            if (delimiterIndex < delimiters.length && arg.equals(delimiters[delimiterIndex])) {
                result.add(current.toString().trim());
                current = new StringBuilder();
                delimiterIndex++;
            } else {
                current.append(arg).append(" ");
            }
        }
        // Add the final segment
        result.add(current.toString().trim());

        return result.toArray(new String[0]);
    }
}
