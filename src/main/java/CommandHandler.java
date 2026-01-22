import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CommandHandler {
    private final Map<String, Function<String[], String>> commands;
    private final BiConsumer<String, String[]> onRespond;
    private final List<Task> tasks;

    private static final String TODO_ERROR = "The description of a todo cannot be empty. Correct usage: todo <description>";
    private static final String DEADLINE_ERROR = "Deadline must have a /by clause. Correct usage: deadline <description> /by <due date>";
    private static final String EVENT_ERROR = "Event must have /from and /to clauses. Correct usage: event <description> /from <start time> /to <end time>";
    private static final String TASK_NUM_ERROR = "Please provide a valid task number.";

    public CommandHandler(BiConsumer<String, String[]> onRespond) {
        this.commands = Map.of(
                "todo", this::handleTodo,
                "deadline", this::handleDeadline,
                "event", this::handleEvent,
                "list", this::handleList,
                "mark", this::handleMark,
                "unmark", this::handleUnmark);
        this.onRespond = onRespond;
        this.tasks = new ArrayList<>();
    }

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

    private String handleTodo(String[] args) {
        String description = String.join(" ", args);
        if (description.isBlank()) {
            return TODO_ERROR;
        }

        Task newTask = new Todo(description);
        tasks.add(newTask);
        onRespond.accept("Got it. I've added this todo:", new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    private String handleDeadline(String[] args) {
        int i;
        StringBuilder descBd = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("/by")) {
                break;
            }
            descBd.append(args[i]).append(" ");
        }
        if (descBd.toString().isBlank()) {
            return DEADLINE_ERROR;
        }
        StringBuilder byBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            byBd.append(args[i]).append(" ");
        }
        if (byBd.toString().isBlank()) {
            return DEADLINE_ERROR;
        }

        Task newTask = new Deadline(descBd.toString().trim(), byBd.toString().trim());
        tasks.add(newTask);
        onRespond.accept(
                "Got it. I've added this deadline:",
                new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    private String handleEvent(String[] args) {
        int i;
        StringBuilder descBd = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("/from")) {
                break;
            }
            descBd.append(args[i]).append(" ");
        }
        if (descBd.toString().isBlank()) {
            return EVENT_ERROR;
        }
        StringBuilder fromBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            if (args[i].equals("/to")) {
                break;
            }
            fromBd.append(args[i]).append(" ");
        }
        if (fromBd.toString().isBlank()) {
            return EVENT_ERROR;
        }
        StringBuilder toBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            toBd.append(args[i]).append(" ");
        }
        if (toBd.toString().isBlank()) {
            return EVENT_ERROR;
        }

        Task newTask = new Event(
                descBd.toString().trim(),
                fromBd.toString().trim(),
                toBd.toString().trim());
        tasks.add(newTask);
        onRespond.accept(
                "Got it. I've added this event:",
                new String[] { newTask.toString(), getTaskCountMsg() });
        return null;
    }

    private String handleList(String[] args) {
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

    private String handleMark(String[] args) {
        int idx;
        try {
            idx = Integer.parseInt(args[0]) - 1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return TASK_NUM_ERROR;
        }
        if (idx < 0 || idx >= tasks.size()) {
            return TASK_NUM_ERROR;
        }

        Task t = tasks.get(idx);
        t.mark();
        onRespond.accept("Bravo! You did it :D", new String[] { t.toString() });
        return null;
    }

    private String handleUnmark(String[] args) {
        int idx;
        try {
            idx = Integer.parseInt(args[0]) - 1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return TASK_NUM_ERROR;
        }
        if (idx < 0 || idx >= tasks.size()) {
            return TASK_NUM_ERROR;
        }
        Task t = tasks.get(idx);
        t.unmark();
        onRespond.accept("Bomb you, why never do properly?", new String[] { t.toString() });
        return null;
    }

    private String getTaskCountMsg() {
        return String.format("You have %d tasks in total now.", tasks.size());
    }
}
