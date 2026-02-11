package ibatun.handling;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Consumer;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

final class FindHandler extends Handler {
    FindHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "find".equals(command);
    }

    @Override
    void handle(String[] args) {
        List<Task> matchedTasks;
        try {
            matchedTasks = store
                    .list()
                    .stream()
                    .filter(task -> Arrays.stream(args).anyMatch(keyword -> task.getName().contains(keyword)))
                    .toList();
        } catch (IbatunException e) {
            fail(e);
            return;
        }

        if (matchedTasks.isEmpty()) {
            succeed("No matching tasks found.");
        } else {
            String header = "Here are the matching tasks in your list:\n";
            String body = IntStream
                    .range(0, matchedTasks.size())
                    .mapToObj(i -> String.format("%d. %s", i + 1, matchedTasks.get(i)))
                    .collect(Collectors.joining("\n"));
            succeed(header + body + "\n");
        }
    }
}
