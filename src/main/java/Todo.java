public class Todo extends Task {
    public Todo(String name) {
        super(name);
    }

    @Override
    public String ser() {
        String normName = name.replace("\\", "\\\\").replace("|", "\\|");
        return String.format("T|%s|%s", normName, (done ? "1" : "0"));
    }

    public static Todo deser(String input) throws IbatunException {
        if (input.isBlank() || input.charAt(0) != 'T') {
            throw new TaskDeserException();
        }
        String[] parts = input.split("\\|");
        if (parts.length != 3) {
            throw new TaskDeserException();
        }
        String name = parts[1].replace("\\|", "|").replace("\\\\", "\\");
        Todo todo = new Todo(name);
        switch (parts[2]) {
            case "0":
                break;
            case "1":
                todo.mark();
                break;
            default:
                throw new TaskDeserException();
        }
        return todo;
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
