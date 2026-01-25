package ibatun.core.tasks;

public abstract sealed class Task permits Todo, Deadline, Event {
    protected String name;
    protected boolean isDone;

    protected Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public void mark() {
        this.isDone = true;
    }

    public void unmark() {
        this.isDone = false;
    }

    public String getName() {
        return this.name;
    }

    public abstract String serialise();

    @Override
    public String toString() {
        return String.format("%s %s", (isDone ? "[X]" : "[ ]"), name);
    }
}
