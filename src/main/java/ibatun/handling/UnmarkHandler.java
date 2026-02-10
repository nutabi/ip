package ibatun.handling;

import ibatun.core.TaskStore;
import ibatun.core.tasks.Task;

/**
 * Handler for the "unmark" command, which unmarks a task as not done.
 */
final class UnmarkHandler extends Handler {
    UnmarkHandler(TaskStore store, java.util.function.Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "unmark".equals(command);
    }

    @Override
    void handle(String[] args) {
        if (args.length != 2) {
            fail("Please provide a valid task number.");
            return;
        }

        try {
            int taskNum = Integer.parseInt(args[1]) - 1;
            Task t = store.getTask(taskNum);
            if (!t.isDone()) {
                fail("This task is already unmarked:\n  " + t.toString());
                return;
            }
            store.modifyTask(taskNum, Task::unmark);
            succeed("Nice! I've unmarked this task as done:\n  " + t.toString());
        } catch (NumberFormatException e) {
            fail("Please provide a valid task number.");
        } catch (IndexOutOfBoundsException e) {
            fail("Task number out of range.");
        }
    }
}
