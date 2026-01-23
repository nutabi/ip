package ibatun.ui;

import java.util.Scanner;

import ibatun.core.CommandHandler;
import ibatun.core.TaskStore;
import ibatun.errors.IbatunException;

public class IbatunCLI {
    public static final String BOT_NAME = "Ibatun";
    public static final String INDENT = "    ";
    public static final String LINE = INDENT + "――――――――――――――――――――――――――――――――――――――――――";
    public static final Scanner STDIN = new Scanner(System.in);

    public static void main(String[] args) {
        TaskStore store = new TaskStore("data.txt", IbatunCLI::respond);
        CommandHandler handler = new CommandHandler(IbatunCLI::respond, store);

        greet();
        String[] input = prompt();
        while (true) {
            try {
                if (!handler.handle(input)) {
                    break;
                }
            } catch (IbatunException e) {
                respond(e.getMessage());
            }
            input = prompt();
        }
        farewell();
    }

    private static String[] prompt() {
        return STDIN.nextLine().split(" ");
    }

    private static void greet() {
        respond(
                String.format("Wassup! I'm %s.", BOT_NAME),
                "How do I help ya?");
    }

    private static void farewell() {
        respond("Baii. See you soon!");
    }

    private static void respond(String title, String... response) {
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
