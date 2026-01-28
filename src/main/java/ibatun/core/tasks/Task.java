package ibatun.core.tasks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Todo.class, name = "todo"),
    @JsonSubTypes.Type(value = Deadline.class, name = "deadline"),
    @JsonSubTypes.Type(value = Event.class, name = "event") })
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

    @Override
    public String toString() {
        return String.format("%s %s", (isDone ? "[X]" : "[ ]"), name);
    }
}
