import java.util.Scanner;

public class Ibatun {
    public static final String BOT_NAME = "Ibatun";
    public static final String INDENT = "    ";
    public static final String LINE = INDENT + "――――――――――――――――――――――――――――――――――――――――――";
    public static final Scanner STDIN = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exiting = false;
        ArrayList<String> tasks = new ArrayList<>();

        greet();
        while (!exiting) {
            String input = prompt();
            switch (input.toLowerCase()) {
                case "bye":
                    exiting = true;
                    break;
                case "list":
                    handleList(tasks);
                    break;
                default:
                    // Treat any other input as a task to be added
                    tasks.add(input);
                    respond(String.format("Got it. I've added this task: %s", input));
                break;
            }
            // Echo user input for demonstration
            respond(input);
        }
        farewell();
    }

    static void handleList(ArrayList<String> tasks) {
    }

    static String prompt() {
        return STDIN.nextLine();
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

    static void respond(String... response) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append("\n");
        for (String res : response) {
            sb.append(INDENT).append(res).append("\n");
        }
        sb.append(LINE).append("\n");
        System.out.print(sb.toString());
    }
}
