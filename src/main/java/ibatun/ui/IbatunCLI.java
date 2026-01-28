package ibatun.ui;

import java.util.Scanner;

import ibatun.core.CommandHandler;
import ibatun.core.TaskStore;
import ibatun.errors.IbatunException;

/**
 * The command-line interface for the Ibatun application.
 *
 * @author Binh
 * @version 1.0
 */
public class IbatunCli {
    /**
     * The name of the bot.
     */
    public static final String BOT_NAME = "Ibatun";

    /**
     * Indentation string for formatting responses.
     */
    private static final String INDENT = "    ";

    /**
     * Line separator for formatting responses.
     */
    private static final String LINE = INDENT + "――――――――――――――――――――――――――――――――――――――――――";

    /**
     * Standard input scanner.
     */
    private static final Scanner STDIN = new Scanner(System.in);

    /**
     * Main method to run the Ibatun CLI application.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        TaskStore store = new TaskStore("data.local.json", IbatunCli::respond);
        CommandHandler handler = new CommandHandler(IbatunCli::respond, store);

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

    /**
     * Prompts the user for input.
     *
     * @return The user input split into command and arguments
     */
    private static String[] prompt() {
        return STDIN.nextLine().split(" ");
    }

    /**
     * Greets the user.
     */
    private static void greet() {
        respond(String.format("Wassup! I'm %s.", BOT_NAME), "How do I help ya?");
    }

    /**
     * Bids farewell to the user.
     */
    private static void farewell() {
        respond("Baii. See you soon!");
    }

    /**
     * Responds to the user with a formatted message.
     *
     * @param title    The title of the response
     * @param response The response lines
     */
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
