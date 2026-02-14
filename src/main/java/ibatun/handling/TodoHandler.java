package ibatun.handling;

import java.util.function.Consumer;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Todo;

/**
 * Handles creation of {@code Todo} tasks.
 *
 * @see Todo
 */
final class TodoHandler extends Handler {
    /**
     * Constructs a TodoHandler.
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
            fail("The description of a todo cannot be empty. Even jokes need punchlines.");
            return;
        }
        String description = String.join(" ", args);
        if (description.isBlank()) {
            fail("The description of a todo cannot be empty. Even jokes need punchlines.");
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
        succeed("Got it. I've added this todo:\n    " + todo.toString() + "\nI will nag you later.");
        return;
    }
}
