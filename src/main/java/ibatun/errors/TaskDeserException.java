package ibatun.errors;

public class TaskDeserException extends IbatunException {
    public TaskDeserException() {
        super("Task deserialisation failed");
    }
}
