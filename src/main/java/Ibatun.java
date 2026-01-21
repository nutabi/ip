import java.util.Scanner;

public class Ibatun {
    public static final String BOT_NAME = "Ibatun";
    public static final String LINE = "――――――――――――――――――――――――――――――――――――――――――";
    public static final Scanner STDIN = new Scanner(System.in);

    public static void main(String[] args) {
        greet();
        farewell();
    }

    static String prompt() {
        System.out.print("> ");
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
            sb.append(res).append("\n");
        }
        sb.append(LINE).append("\n");
        System.out.print(sb.toString());
    }
}
