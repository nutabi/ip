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
            action.accept(input);
        } else {
            onRespond.accept("I don't get what you mean :(", new String[0]);
        }
        return true;
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
        int idx = Integer.parseInt(args[1]) - 1;
        Task t = tasks.get(idx);
        t.mark();
        onRespond.accept("Bravo! You did it :D", new String[]{t.toString()});
    }

    private void handleUnmark(String[] args) {
        int idx = Integer.parseInt(args[1]) - 1;
        Task t = tasks.get(idx);
        t.unmark();
        onRespond.accept("Alright, I've marked this task as not done yet.", new String[]{t.toString()});
    }
}
