public abstract class Task {
    protected String name;
    protected boolean done;

    protected Task(String name) {
        this.name = name;
        this.done = false;
    }

    public boolean done() {
        return done;
    }

    public void mark() {
        this.done = true;
    }

    public void unmark() {
        this.done = false;
    }

    public String getName() {
        return this.name;
    }

    public abstract String ser();

    @Override
    public String toString() {
        return String.format("%s %s", (done ? "[X]" : "[ ]"), name);
    }
}
