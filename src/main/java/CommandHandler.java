import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandHandler {
    private final Map<String, Consumer<String[]>> commands;
    private final BiConsumer<String, String[]> onRespond;
    private final List<Task> tasks;

    public CommandHandler(BiConsumer<String, String[]> onRespond) {
        this.commands = Map.of(
            "todo", this::handleTodo,
            "deadline", this::handleDeadline,
            "event", this::handleEvent,
            "list", this::handleList,
            "mark", this::handleMark,
            "unmark", this::handleUnmark
        );
        this.onRespond = onRespond;
        this.tasks = new ArrayList<>();
    }

    public boolean handle(String[] input) {
        String command = input[0].toLowerCase();

        // Check exit command
        if (command.equals("bye")) {
            return false;
        }

        // Dispatch to appropriate handler
        Consumer<String[]> action = commands.get(command);
        if (action != null) {
            action.accept(java.util.Arrays.copyOfRange(input, 1, input.length));
        } else {
            onRespond.accept("I don't get what you mean :(", new String[0]);
        }
        return true;
    }

    private void handleTodo(String[] args) {
        String description = String.join(" ", args);
        Task newTask = new Todo(description);
        tasks.add(newTask);
        onRespond.accept("Got it. I've added this todo:", new String[]{newTask.toString()});
    }

    private void handleDeadline(String[] args) {
        int i;
        StringBuilder descBd = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("/by")) {
                break;
            }
            descBd.append(args[i]).append(" ");
        }
        StringBuilder byBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            byBd.append(args[i]).append(" ");
        }

        Task newTask = new Deadline(descBd.toString().trim(), byBd.toString().trim());
        tasks.add(newTask);
        onRespond.accept("Got it. I've added this deadline:", new String[]{newTask.toString()});
    }

    private void handleEvent(String[] args) {
        int i;
        StringBuilder descBd = new StringBuilder();
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("/from")) {
                break;
            }
            descBd.append(args[i]).append(" ");
        }
        StringBuilder fromBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            if (args[i].equals("/to")) {
                break;
            }
            fromBd.append(args[i]).append(" ");
        }
        StringBuilder toBd = new StringBuilder();
        for (i = i + 1; i < args.length; i++) {
            toBd.append(args[i]).append(" ");
        }

        Task newTask = new Event(
            descBd.toString().trim(),
            fromBd.toString().trim(),
            toBd.toString().trim()
        );
        tasks.add(newTask);
        onRespond.accept("Got it. I've added this event:", new String[]{newTask.toString()});
    }

    private void handleList(String[] args) {
        if (tasks.isEmpty()) {
            onRespond.accept("You ain't got no task.", new String[0]);
        } else {
            List<String> indexedTasks = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                indexedTasks.add(String.format("%d. %s", i + 1, tasks.get(i)));
            }
            onRespond.accept("Here are your tasks:", indexedTasks.toArray(new String[0]));
        }
    }

    private void handleMark(String[] args) {
        int idx = Integer.parseInt(args[0]) - 1;
        Task t = tasks.get(idx);
        t.mark();
        onRespond.accept("Bravo! You did it :D", new String[]{t.toString()});
    }

    private void handleUnmark(String[] args) {
        int idx = Integer.parseInt(args[0]) - 1;
        Task t = tasks.get(idx);
        t.unmark();
        onRespond.accept("Alright, I've marked this task as not done yet.", new String[]{t.toString()});
    }
}
