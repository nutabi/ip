package ibatun.core;

public abstract class Task {
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

    public abstract String ser();

    @Override
    public String toString() {
        return String.format("%s %s", (isDone ? "[X]" : "[ ]"), name);
    }
}
