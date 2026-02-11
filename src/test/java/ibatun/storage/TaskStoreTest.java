package ibatun.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ibatun.errors.IbatunTaskNotFoundException;
import ibatun.tasks.Task;
import ibatun.tasks.Todo;

public class TaskStoreTest {
    private static final class DummyStore extends TaskStore {
        DummyStore(List<Task> tasks) {
            super(tasks);
        }
    }

    @Test
    public void get_validIndex_returnsTask() throws Exception {
        Task task = new Todo("read");
        TaskStore store = new DummyStore(new ArrayList<>(List.of(task)));
        assertEquals(task, store.get(0));
    }

    @Test
    public void get_outOfRange_throws() {
        TaskStore store = new DummyStore(new ArrayList<>());
        assertThrows(IbatunTaskNotFoundException.class, () -> store.get(0));
    }

    @Test
    public void list_returnsUnmodifiableList() throws Exception {
        TaskStore store = new DummyStore(new ArrayList<>(List.of(new Todo("read"))));
        List<Task> tasks = store.list();
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new Todo("write")));
    }

    @Test
    public void add_appendsTask() throws Exception {
        TaskStore store = new DummyStore(new ArrayList<>());
        Task task = new Todo("read");
        store.add(task);
        assertEquals(task, store.get(0));
    }

    @Test
    public void remove_validIndex_deletesTask() throws Exception {
        TaskStore store = new DummyStore(new ArrayList<>(List.of(new Todo("read"))));
        store.remove(0);
        assertThrows(IbatunTaskNotFoundException.class, () -> store.get(0));
    }

    @Test
    public void remove_outOfRange_throws() {
        TaskStore store = new DummyStore(new ArrayList<>());
        assertThrows(IbatunTaskNotFoundException.class, () -> store.remove(0));
    }

    @Test
    public void modify_validIndex_appliesModifier() throws Exception {
        TaskStore store = new DummyStore(new ArrayList<>(List.of(new Todo("read"))));
        store.modify(0, Task::mark);
        assertEquals(true, store.get(0).isDone());
    }

    @Test
    public void modify_outOfRange_throws() {
        TaskStore store = new DummyStore(new ArrayList<>());
        assertThrows(IbatunTaskNotFoundException.class, () -> store.modify(0, Task::mark));
    }
}
