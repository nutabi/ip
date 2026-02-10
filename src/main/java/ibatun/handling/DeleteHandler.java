package ibatun.handling;

import java.util.function.Consumer;

import ibatun.core.TaskStore;
import ibatun.core.tasks.Task;

/**
 * Handler for the "delete" command, which deletes a task from the task store.
 */
final class DeleteHandler extends Handler {
    DeleteHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "delete".equals(command);
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
            store.removeTask(taskNum);
            succeed("Noted. I've removed this task:\n  "
                    + t.toString()
                    + "\nNow you have "
                    + store.listTasks().size()
                    + " tasks in the list.");
        } catch (NumberFormatException e) {
            fail("Please provide a valid task number.");
        } catch (IndexOutOfBoundsException e) {
            fail("Task number out of range.");
        }
    }

}
