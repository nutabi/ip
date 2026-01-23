import java.time.LocalDateTime;

public class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }

    @Override
    public String ser() {
        return String.format("D|%s|%s|%s", name, (done ? "1" : "0"), by);
    }

    public static Deadline deser(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'D') {
            throw new TaskDeserException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 4) {
            throw new TaskDeserException();
        }
        LocalDateTime by = LocalDateTime.parse(parts[3]);
        Deadline deadline = new Deadline(parts[1], by);
        switch (parts[2]) {
            case "0":
                break;
            case "1":
                deadline.mark();
                break;
            default:
                throw new TaskDeserException();
        }
        return deadline;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), DatetimeConverter.format(by));
    }
}
