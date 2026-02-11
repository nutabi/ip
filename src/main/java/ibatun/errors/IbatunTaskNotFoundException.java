package ibatun.errors;

public class IbatunTaskNotFoundException extends IbatunException {
    /**
     * Constructs a TaskNotFoundException with a default error message.
     */
    public IbatunTaskNotFoundException() {
        super("The specified task was not found.");
    }

}
