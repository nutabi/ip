public class Event extends Task {
    protected String from;
    protected String to;

    public Event(String name, String from, String to) {
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
        Event event = new Event(parts[1], parts[3], parts[4]);
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
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }
}
