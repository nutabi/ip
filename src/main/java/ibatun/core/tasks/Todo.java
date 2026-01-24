package ibatun.core.tasks;

import ibatun.core.Task;
import ibatun.errors.IbatunException;
import ibatun.errors.TaskSerialisationException;

public class Todo extends Task {
    public Todo(String name) {
        super(name);
    }

    @Override
    public String serialise() {
        return String.format("T|%s|%s", name, (isDone() ? "1" : "0"));
    }

    public static Todo deserialise(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'T') {
            throw new TaskSerialisationException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 3) {
            throw new TaskSerialisationException();
        }
        Todo todo = new Todo(parts[1]);
        switch (parts[2]) {
        case "0":
            break;
        case "1":
            todo.mark();
            break;
        default:
            throw new TaskSerialisationException();
        }
        return todo;
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
