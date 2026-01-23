package ibatun.core.tasks;

import java.time.LocalDateTime;

import ibatun.core.Task;
import ibatun.errors.IbatunException;
import ibatun.errors.TaskDeserException;
import ibatun.util.DatetimeConverter;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String ser() {
        return String.format("E|%s|%s|%s|%s", name, (done ? "1" : "0"), from, to);
    }

    public static Event deser(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'E') {
            throw new TaskDeserException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            throw new TaskDeserException();
        }
        LocalDateTime from = LocalDateTime.parse(parts[3]);
        LocalDateTime to = LocalDateTime.parse(parts[4]);
        Event event = new Event(parts[1], from, to);
        switch (parts[2]) {
            case "0":
                break;
            case "1":
                event.mark();
                break;
            default:
                throw new TaskDeserException();
        }
        return event;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), DatetimeConverter.format(from),
                DatetimeConverter.format(to));
    }
}
