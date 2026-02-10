package ibatun.handling;

import java.util.function.Consumer;

import ibatun.core.TaskStore;

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

        // Handle empty task list
        if (store.listTasks().isEmpty()) {
            succeed("You ain't got no task.");
            return;
        }

        for (int i = 0; i < store.listTasks().size(); i++) {
            response.append(i + 1).append(". ").append(store.getTask(i).toString()).append("\n");
        }
        succeed(response.toString().trim());
    }

}
