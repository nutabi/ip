public class Deadline extends Task {
    protected String by;

    public Deadline(String name, String by) {
        super(name);
        this.by = by;
    }

    @Override
    public String ser() {
        String normName = name.replace("\\", "\\\\").replace("|", "\\|");
        String normBy = by.replace("\\", "\\\\").replace("|", "\\|");
        return String.format("D|%s|%s|%s", normName, (done ? "1" : "0"), normBy);
    }

    public static Deadline deser(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'D') {
            throw new TaskDeserException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 4) {
            throw new TaskDeserException();
        }
        String name = parts[1].replace("\\|", "|").replace("\\\\", "\\");
        String by = parts[3].replace("\\|", "|").replace("\\\\", "\\");
        Deadline deadline = new Deadline(name, by);
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
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }
}
