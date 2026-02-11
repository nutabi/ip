package ibatun.handling;

import java.util.List;
import java.util.function.Consumer;

import ibatun.storage.TaskStore;
import ibatun.tasks.Task;
import ibatun.errors.IbatunException;

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
            matchedTasks = store.list().stream().filter(task -> {
                for (String keyword : args) {
                    if (task.getName().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }).toList();
        } catch (IbatunException e) {
            fail(e);
            return;
        }

        if (matchedTasks.isEmpty()) {
            succeed("No matching tasks found.");
        } else {
            StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
            for (int i = 0; i < matchedTasks.size(); i++) {
                sb.append(String.format("%d. %s\n", i + 1, matchedTasks.get(i)));
            }
            succeed(sb.toString());
        }
    }
}
