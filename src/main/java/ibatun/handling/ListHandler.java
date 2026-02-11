package ibatun.handling;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

/**
 * Handler for the "list" command, which lists all tasks in the task store.
 */
final class ListHandler extends Handler {
    ListHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "list".equals(command);
    }

    @Override
    void handle(String[] args) {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:\n");

        // Get task list
        List<Task> tasks;
        try {
            tasks = store.list();
        } catch (IbatunException e) {
            fail(e);
            return;
        }

        // Handle empty task list
        if (tasks.isEmpty()) {
            succeed("You ain't got no task.");
            return;
        }

        String body = IntStream
                .range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.joining("\n"));
        succeed(response.append(body).toString().trim());
    }

}
