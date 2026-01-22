import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Ibatun {
    public static final String BOT_NAME = "Ibatun";
    public static final String INDENT = "    ";
    public static final String LINE = INDENT + "――――――――――――――――――――――――――――――――――――――――――";
    public static final Scanner STDIN = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exiting = false;
        ArrayList<Task> tasks = new ArrayList<>();

        greet();
        while (!exiting) {
            String[] input = prompt();
            switch (input[0].toLowerCase()) {
                case "bye":
                    exiting = true;
                    break;
                case "list":
                    handleList(tasks);
                    break;
                case "mark":
                    handleMark(tasks.get(Integer.parseInt(input[1]) - 1));
                    break;
                case "unmark":
                    handleUnmark(tasks.get(Integer.parseInt(input[1]) - 1));
                    break;
                default:
                    // Treat any other input as a task to be added
                    Task t = new Task(String.join(" ", input));
                    tasks.add(t);
                    respond("Gotchu. I've added this task:", t.toString());
                    break;
            }
        }
        farewell();
    }

    static void handleList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            respond("You ain't got no task.");
        } else {
            List<String> indexedTasks = IntStream.range(0, tasks.size())
                .mapToObj(i -> String.format("%d. %s", i + 1, tasks.get(i)))
                .toList();
            respond("Here are your tasks:", indexedTasks.toArray(new String[0]));
        }
    }

    static void handleMark(Task t) {
        t.mark();
        respond("Bravo! You did it :D", t.toString());
    }

    static void handleUnmark(Task t) {
        t.unmark();
        respond("Alright, I've marked this task as not done yet.", t.toString());
    }

    static String[] prompt() {
        return STDIN.nextLine().split(" ");
    }

    static void greet() {
        respond(
            String.format("Wassup! I'm %s.", BOT_NAME),
            "How do I help ya?"
        );
    }

    static void farewell() {
        respond("Baii. See you soon!");
    }

    static void respond(String title, String... response) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("\n");
        sb.append(INDENT).append(title).append("\n");
        for (String res : response) {
            sb.append(INDENT).append(res).append("\n");
        }
        sb.append(LINE).append("\n");
        System.out.print(sb.toString());
    }
}
