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
        String normName = name.replace("|", "\\|");
        return String.format("E|%s|%s|%s|%s", normName, (done ? "1" : "0"), from, to);
    }

    public static Event deser(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'E') {
            throw new TaskDeserException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            throw new TaskDeserException();
        }
        String name = parts[1].replace("\\|", "|");
        String from = parts[3];
        String to = parts[4];
        Event event = new Event(name, from, to);
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
