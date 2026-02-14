package ibatun.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ibatun.tasks.Todo;

public class HandlersTest {
    @Test
    public void todoHandler_emptyArgs_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TodoHandler handler = new TodoHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] {});
        assertTrue(sink.last().contains("todo cannot be empty"));
    }

    @Test
    public void todoHandler_validArgs_addsTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>());
        TodoHandler handler = new TodoHandler(store, sink);

        handler.handle(new String[] { "read", "book" });
        assertEquals(1, store.list().size());
        assertTrue(sink.last().contains("added this todo"));
    }

    @Test
    public void deadlineHandler_missingParts_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        DeadlineHandler handler = new DeadlineHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "submit", "/by" });
        assertTrue(sink.last().contains("deadline command requires"));
    }

    @Test
    public void deadlineHandler_invalidDate_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        DeadlineHandler handler = new DeadlineHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "submit", "/by", "bad-date" });
        assertTrue(sink.last().contains("Invalid date/time"));
    }

    @Test
    public void deadlineHandler_validArgs_addsTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>());
        DeadlineHandler handler = new DeadlineHandler(store, sink);

        handler.handle(new String[] { "submit", "/by", "2026-Feb-11", "12:00" });
        assertEquals(1, store.list().size());
        assertTrue(sink.last().contains("added this deadline"));
    }

    @Test
    public void eventHandler_missingParts_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        EventHandler handler = new EventHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "conf", "/from", "2026-Feb-11" });
        assertTrue(sink.last().contains("event command requires"));
    }

    @Test
    public void eventHandler_endBeforeStart_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        EventHandler handler = new EventHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "conf", "/from", "2026-Feb-11", "10:00", "/to", "2026-Feb-11", "09:00" });
        assertTrue(sink.last().contains("end time cannot be before"));
    }

    @Test
    public void eventHandler_validArgs_addsTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>());
        EventHandler handler = new EventHandler(store, sink);

        handler.handle(new String[] { "conf", "/from", "2026-Feb-11", "09:00", "/to", "2026-Feb-11", "10:00" });
        assertEquals(1, store.list().size());
        assertTrue(sink.last().contains("added this event"));
    }

    @Test
    public void markHandler_invalidNumber_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        MarkHandler handler = new MarkHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "abc" });
        assertTrue(sink.last().contains("valid task number"));
    }

    @Test
    public void markHandler_alreadyDone_fails() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        Todo todo = new Todo("read");
        todo.mark();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>(List.of(todo)));
        MarkHandler handler = new MarkHandler(store, sink);

        handler.handle(new String[] { "1" });
        assertTrue(sink.last().contains("already marked as done"));
    }

    @Test
    public void markHandler_valid_marksTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>(List.of(new Todo("read"))));
        MarkHandler handler = new MarkHandler(store, sink);

        handler.handle(new String[] { "1" });
        assertTrue(store.get(0).isDone());
        assertTrue(sink.last().contains("marked this task as done"));
    }

    @Test
    public void unmarkHandler_alreadyUnmarked_fails() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>(List.of(new Todo("read"))));
        UnmarkHandler handler = new UnmarkHandler(store, sink);

        handler.handle(new String[] { "1" });
        assertTrue(sink.last().contains("already unmarked"));
    }

    @Test
    public void unmarkHandler_valid_unmarksTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        Todo todo = new Todo("read");
        todo.mark();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>(List.of(todo)));
        UnmarkHandler handler = new UnmarkHandler(store, sink);

        handler.handle(new String[] { "1" });
        assertTrue(!store.get(0).isDone());
        assertTrue(sink.last().contains("unmarked this task"));
    }

    @Test
    public void deleteHandler_invalidNumber_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        DeleteHandler handler = new DeleteHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] { "abc" });
        assertTrue(sink.last().contains("valid task number"));
    }

    @Test
    public void deleteHandler_valid_removesTask() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(
                new ArrayList<>(List.of(new Todo("read"), new Todo("write"))));
        DeleteHandler handler = new DeleteHandler(store, sink);

        handler.handle(new String[] { "1" });
        assertEquals(1, store.list().size());
        assertTrue(sink.last().contains("removed this task"));
        assertTrue(sink.last().contains("Now you have 1 tasks"));
    }

    @Test
    public void listHandler_empty_showsMessage() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        ListHandler handler = new ListHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] {});
        assertTrue(sink.last().contains("ain't got no task"));
    }

    @Test
    public void listHandler_withTasks_lists() throws Exception {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(
                new ArrayList<>(List.of(new Todo("read"), new Todo("write"))));
        ListHandler handler = new ListHandler(store, sink);

        handler.handle(new String[] {});
        assertTrue(sink.last().contains("1."));
        assertTrue(sink.last().contains("read"));
        assertTrue(sink.last().contains("2."));
        assertTrue(sink.last().contains("write"));
    }

    @Test
    public void findHandler_noKeywords_fails() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        FindHandler handler = new FindHandler(new TestSupport.InMemoryStore(new ArrayList<>()), sink);

        handler.handle(new String[] {});
        assertTrue(sink.last().contains("at least one keyword"));
    }

    @Test
    public void findHandler_noMatches_showsMessage() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(new ArrayList<>(List.of(new Todo("read"))));
        FindHandler handler = new FindHandler(store, sink);

        handler.handle(new String[] { "write" });
        assertTrue(sink.last().contains("No matching tasks found"));
    }

    @Test
    public void findHandler_matches_lists() {
        TestSupport.ResponseSink sink = new TestSupport.ResponseSink();
        TestSupport.InMemoryStore store = new TestSupport.InMemoryStore(
                new ArrayList<>(List.of(new Todo("read book"), new Todo("write"))));
        FindHandler handler = new FindHandler(store, sink);

        handler.handle(new String[] { "read" });
        assertTrue(sink.last().contains("matching tasks"));
        assertTrue(sink.last().contains("read book"));
    }
}
