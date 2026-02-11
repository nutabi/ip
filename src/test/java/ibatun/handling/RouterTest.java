package ibatun.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ibatun.tasks.Task;

public class RouterTest {
    @Test
    public void route_emptyArgs_returnsTrueAndResponds() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        Router router = new Router(new TestSupport.InMemoryStore(new ArrayList<>()), sink);
        boolean shouldContinue = router.route(new String[] {});

        assertTrue(shouldContinue);
        assertTrue(sink.last().contains("No command provided."));
    }

    @Test
    public void route_bye_returnsFalse() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        Router router = new Router(new TestSupport.InMemoryStore(new ArrayList<>()), sink);
        boolean shouldContinue = router.route(new String[] { "bye" });

        assertFalse(shouldContinue);
    }

    @Test
    public void route_unknownCommand_returnsTrueAndResponds() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        Router router = new Router(new TestSupport.InMemoryStore(new ArrayList<>()), sink);
        boolean shouldContinue = router.route(new String[] { "unknown" });

        assertTrue(shouldContinue);
        assertTrue(sink.last().contains("don't know what that command means"));
    }

    @Test
    public void route_todoPassesRemainingArgs_addsTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>());
        Router router = new Router(store, sink);

        boolean shouldContinue = router.route(new String[] { "todo", "read", "book" });
        assertTrue(shouldContinue);
        assertEquals(1, store.list().size());
        Task task = store.list().get(0);
        assertEquals("read book", task.getName());
    }
}
