package ibatun.handling;

import java.util.function.Consumer;

import ibatun.core.TaskStore;

/**
 * A router class for directing commands to appropriate handlers.
 */
public final class Router {
    private final Consumer<String> onRespond;
    private final Handler[] handlers;

    /**
     * Constructor for Router.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    public Router(TaskStore store, Consumer<String> onRespond) {
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
            onRespond.accept("No command provided.");
            return true;
        } else if (args[0].equals("bye")) {
            return false;
        }

        for (Handler handler : handlers) {
            if (handler.canHandle(args[0])) {
                handler.handle(args);
                return true;
            }
        }
        onRespond.accept("I'm sorry, but I don't know what that command means.");
        return true;
    }
}
