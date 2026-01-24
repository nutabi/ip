package ibatun.errors;

public class TaskSerialisationException extends IbatunException {
    public TaskSerialisationException() {
        super("Task deserialisation failed");
    }
}
