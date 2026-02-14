package ibatun.handling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ibatun.errors.IbatunException;
import ibatun.storage.TaskStore;
import ibatun.tasks.Task;

/**
 * Handles the "find" command, which searches for tasks matching given keywords. Uses fuzzy matching with Levenshtein
 * distance to find similar task names.
 */
final class FindHandler extends Handler {
    /**
     * Constructs a FindHandler.
     *
     * @param store     The task store
     * @param onRespond The consumer function to handle responses
     */
    FindHandler(TaskStore store, Consumer<String> onRespond) {
        super(store, onRespond);
    }

    @Override
    boolean canHandle(String command) {
        return "find".equals(command);
    }

    @Override
    void handle(String[] args) {
        String[] keywords = Arrays.stream(args).filter(keyword -> !keyword.isBlank()).toArray(String[]::new);
        if (keywords.length == 0) {
            fail("Please provide at least one keyword to find. I am good, not psychic.");
            return;
        }

        List<Task> matchedTasks;
        try {
            matchedTasks = store
                    .list()
                    .stream()
                    .map(task -> scoreTask(task, keywords))
                    .filter(scored -> scored.isSimilar)
                    .sorted(Comparator.comparingInt(scored -> scored.score))
                    .map(scored -> scored.task)
                    .toList();
        } catch (IbatunException e) {
            fail(e);
            return;
        }

        if (matchedTasks.isEmpty()) {
            succeed("No matching tasks found. The detective shrugs.");
        } else {
            String header = "Here are the matching tasks in your list:\n";
            String body = IntStream
                    .range(0, matchedTasks.size())
                    .mapToObj(i -> String.format("    %d. %s", i + 1, matchedTasks.get(i)))
                    .collect(Collectors.joining("\n"));
            succeed(header + body + "\n");
        }
    }

    private static ScoredTask scoreTask(Task task, String[] keywords) {
        String name = task.getName().toLowerCase();
        String[] words = name.split("\\s+");

        int bestScore = Integer.MAX_VALUE;
        int bestThreshold = Integer.MAX_VALUE;

        for (String keyword : keywords) {
            String query = keyword.toLowerCase();
            int threshold = Math.max(1, (int) Math.ceil(query.length() * 0.4));
            int minDistance = minDistanceToWords(query, words, name);
            if (minDistance < bestScore) {
                bestScore = minDistance;
                bestThreshold = threshold;
            }
        }

        boolean isSimilar = bestScore <= bestThreshold;
        return new ScoredTask(task, bestScore, isSimilar);
    }

    private static int minDistanceToWords(String keyword, String[] words, String fallback) {
        int best = levenshteinDistance(keyword, fallback);
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            int distance = levenshteinDistance(keyword, word);
            if (distance < best) {
                best = distance;
            }
        }
        return best;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                int replace = dp[i - 1][j - 1] + cost;
                dp[i][j] = Math.min(delete, Math.min(insert, replace));
            }
        }
        return dp[a.length()][b.length()];
    }

    private static final class ScoredTask {
        private final Task task;
        private final int score;
        private final boolean isSimilar;

        private ScoredTask(Task task, int score, boolean isSimilar) {
            this.task = task;
            this.score = score;
            this.isSimilar = isSimilar;
        }
    }
}
