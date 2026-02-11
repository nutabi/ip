package ibatun.handling;

import java.util.List;
import java.util.function.Consumer;

import ibatun.core.tasks.Task;
import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;

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

        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1).append(". ").append(tasks.get(i).toString()).append("\n");
        }
        succeed(response.toString().trim());
    }

}
