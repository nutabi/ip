package ibatun.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for argument parsing and manipulation.
 *
 * @author Binh
 * @version 1.0
 */
public class ArgTools {
    /**
     * Splits the input arguments by the specified delimiters.
     *
     * @param args       The input arguments
     * @param delimiters The delimiters to split by
     * @return The split segments
     */
    public static String[] splitByDelimiters(String[] args, String... delimiters) {
        assert args != null : "Arguments cannot be null";
        assert delimiters != null : "Delimiters cannot be null";

        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int delimiterIndex = 0;

        for (String arg : args) {
            // Check if this arg is the next expected delimiter
            if (delimiterIndex < delimiters.length && arg.equals(delimiters[delimiterIndex])) {
                result.add(current.toString().trim());
                current = new StringBuilder();
                delimiterIndex++;
            } else {
                current.append(arg).append(" ");
            }
        }
        // Add the final segment
        result.add(current.toString().trim());

        return result.toArray(new String[0]);
    }
}
