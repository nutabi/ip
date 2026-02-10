package ibatun.handling;

import java.util.function.Consumer;

import ibatun.core.TaskStore;

/**
 * An abstract handler class that provides a structure for handling commands and responses.
 */
abstract class Handler {
    /**
     * The consumer function to handle responses.
     */
    private final Consumer<String> onRespond;

    /**
     * The task store for managing tasks.
     */
    protected final TaskStore store;

    /**
     * Constructs a Handler with the specified task store and response handler.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    protected Handler(TaskStore store, Consumer<String> onRespond) {
        this.store = store;
        this.onRespond = onRespond;
    }

    /**
     * Sends a success message to the response handler.
     *
     * @param message The success message
     */
    protected void succeed(String message) {
        onRespond.accept(message);
    }

    /**
     * Sends a failure message to the response handler.
     *
     * @param message The failure message
     */
    protected void fail(String message) {
        onRespond.accept("Oops! Something went wrong :D\n\n" + message);
    }

    /**
     * Determines if this handler can process the given command.
     *
     * @param command The command string
     * @return True if the handler can process the command, false otherwise
     */
    abstract boolean canHandle(String command);

    /**
     * Handles the command with the given arguments.
     *
     * @param args The arguments for the command
     */
    abstract void handle(String[] args);
}
