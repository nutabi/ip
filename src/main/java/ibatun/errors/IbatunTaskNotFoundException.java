package ibatun.errors;

/**
 * Exception thrown when a specified task is not found in the task store.
 */
public class IbatunTaskNotFoundException extends IbatunException {
    /**
     * Constructs a TaskNotFoundException with a default error message.
     */
    public IbatunTaskNotFoundException() {
        super("The specified task was not found.");
    }

}
