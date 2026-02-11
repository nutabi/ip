package ibatun.handling;

import java.util.function.Consumer;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Todo;

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
        if (args.length == 0) {
            fail("The description of a todo cannot be empty.");
            return;
        }
        String description = String.join(" ", args);
        if (description.isBlank()) {
            fail("The description of a todo cannot be empty.");
            return;
        }

        // Add the todo task to the store
        Todo todo = new Todo(description);
        try {
            store.add(todo);
        } catch (IbatunException e) {
            fail(e.getMessage());
            return;
        }
        succeed("Got it. I've added this todo: " + todo.toString());
        return;
    }
}
