package ibatun.handling;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

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
        if (args.length != 1 || args[0].isBlank()) {
            fail("Please provide a valid task number.");
            return;
        }

        try {
            int taskNum = Integer.parseInt(args[0]) - 1;
            Task t = store.get(taskNum);
            if (!t.isDone()) {
                fail("This task is already unmarked:\n  " + t.toString());
                return;
            }
            store.modify(taskNum, Task::unmark);
            succeed("Nice! I've unmarked this task as done:\n  " + t.toString());
        } catch (NumberFormatException e) {
            fail("Please provide a valid task number.");
        } catch (IndexOutOfBoundsException e) {
            fail("Task number out of range.");
        } catch (IbatunException e) {
            fail(e);
        }
    }
}
