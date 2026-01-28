package ibatun.core.tasks;

import java.time.LocalDateTime;

import ibatun.util.DatetimeConverter;

public final class Deadline extends Task {
    private LocalDateTime by;

    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), DatetimeConverter.format(by));
    }
}
