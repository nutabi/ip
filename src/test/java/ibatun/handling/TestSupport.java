package ibatun.handling;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

final class TestSupport {
    static final class InMemoryStore extends TaskStore {
        InMemoryStore(List<Task> tasks) {
            super(tasks);
        }
    }

    static final class ResponseSink implements Consumer<String> {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void accept(String message) {
            messages.add(message);
        }

        String last() {
            if (messages.isEmpty()) {
                return "";
            }
            return messages.get(messages.size() - 1);
        }
    }

    private TestSupport() {
    }
}
