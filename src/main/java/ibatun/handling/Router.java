package ibatun.handling;

import java.util.Arrays;
import java.util.function.Consumer;

import ibatun.storage.TaskStore;

/**
 * Routes commands to appropriate handlers.
 */
public final class Router {
    private final Consumer<String> onRespond;
    private final Handler[] handlers;

    /**
     * Constructs a Router.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    public Router(TaskStore store, Consumer<String> onRespond) {
        assert store != null : "TaskStore cannot be null";
        assert onRespond != null : "Response handler cannot be null";

        this.onRespond = onRespond;
        this.handlers = new Handler[] { new TodoHandler(store, onRespond), new DeadlineHandler(store, onRespond),
            new EventHandler(store, onRespond), new ListHandler(store, onRespond), new MarkHandler(store, onRespond),
            new UnmarkHandler(store, onRespond), new DeleteHandler(store, onRespond),
            new FindHandler(store, onRespond) };
    }

    /**
     * Routes the command to the appropriate handler.
     *
     * @param args The command arguments
     */
    public boolean route(String[] args) {
        if (args.length == 0) {
            onRespond.accept("No command provided. My crystal ball is on break.");
            return true;
        }

        String command = args[0];
        if (command.equals("bye")) {
            return false;
        }

        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);

        for (Handler handler : handlers) {
            if (handler.canHandle(command)) {
                handler.handle(remainingArgs);
                return true;
            }
        }
        onRespond.accept("I don't know what that command means. Try again, wizard.");
        return true;
    }
}
