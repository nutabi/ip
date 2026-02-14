package ibatun.handling;

import java.util.function.Consumer;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

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
        if (args.length != 1 || args[0].isBlank()) {
            fail("Please provide a valid task number. I only count on purpose.");
            return;
        }

        try {
            int taskNum = Integer.parseInt(args[0]) - 1;
            Task t = store.get(taskNum);
            store.remove(taskNum);
            succeed("Poof. I've removed this task:\n    "
                    + t.toString()
                    + "\nNow you have "
                    + store.list().size()
                    + " tasks in the list.");
        } catch (NumberFormatException e) {
            fail("Please provide a valid task number. I only count on purpose.");
        } catch (IndexOutOfBoundsException e) {
            fail("Task number out of range. My fingers only go so far.");
        } catch (IbatunException e) {
            fail(e);
        }
    }

}
