package ibatun.handling;

import java.util.function.Consumer;

import ibatun.core.TaskStore;
import ibatun.core.tasks.Todo;

/**
 * A handler for creating {@code Todo} tasks.
 *
 * @see Todo
 */
final class TodoHandler extends Handler {
    /**
     * Constructor for TodoHandler.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    TodoHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "todo".equals(command);
    }

    @Override
    void handle(String[] args) {
        String description = String.join(" ", args);
        if (description.isEmpty()) {
            fail("The description of a todo cannot be empty.");
            return;
        }

        // Add the todo task to the store
        Todo todo = new Todo(description);
        store.addTask(todo);
        succeed("Got it. I've added this todo: " + todo.toString());
        return;
    }
}
