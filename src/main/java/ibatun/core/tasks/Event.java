package ibatun.core.tasks;

import java.time.LocalDateTime;

import ibatun.util.DatetimeConverter;

public final class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    public Event(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String
                .format("[E]%s (from: %s to: %s)", super.toString(), DatetimeConverter.format(from),
                        DatetimeConverter.format(to));
    }
}
