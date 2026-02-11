package ibatun.handling;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

/**
 * Handler for the "mark" command, which marks a task as done.
 */
final class MarkHandler extends Handler {
    MarkHandler(TaskStore store, java.util.function.Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "mark".equals(command);
    }

    @Override
    void handle(String[] args) {
        if (args.length != 2) {
            fail("Please provide a valid task number.");
            return;
        }

        try {
            int taskNum = Integer.parseInt(args[1]) - 1;
            Task t = store.get(taskNum);
            if (t.isDone()) {
                fail("This task is already marked as done:\n  " + t.toString());
                return;
            }
            store.modify(taskNum, Task::mark);
            succeed("Nice! I've marked this task as done:\n  " + t.toString());
        } catch (NumberFormatException e) {
            fail("Please provide a valid task number.");
        } catch (IndexOutOfBoundsException e) {
            fail("Task number out of range.");
        } catch (IbatunException e) {
            fail(e);
        }
    }
}
