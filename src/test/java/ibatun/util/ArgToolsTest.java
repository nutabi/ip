package ibatun.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class ArgToolsTest {
    @Test
    public void splitByDelimiters_singleDelimiter_success() {
        String[] args = { "read", "book", "/by", "2026-Jan-01", "12:00" };
        String[] result = ArgTools.splitByDelimiters(args, "/by");
        assertArrayEquals(new String[] { "read book", "2026-Jan-01 12:00" }, result);
    }

    @Test
    public void splitByDelimiters_multipleDelimiters_success() {
        String[] args = { "party", "/from", "Jan", "1", "/to", "Jan", "2" };
        String[] result = ArgTools.splitByDelimiters(args, "/from", "/to");
        assertArrayEquals(new String[] { "party", "Jan 1", "Jan 2" }, result);
    }

    @Test
    public void splitByDelimiters_delimiterAtEnd_returnsEmptySegment() {
        String[] args = { "task", "/by" };
        String[] result = ArgTools.splitByDelimiters(args, "/by");
        assertArrayEquals(new String[] { "task", "" }, result);
    }

    @Test
    public void splitByDelimiters_noDelimiter_returnsSingleSegment() {
        String[] args = { "just", "some", "text" };
        String[] result = ArgTools.splitByDelimiters(args, "/by");
        assertArrayEquals(new String[] { "just some text" }, result);
    }

    @Test
    public void splitByDelimiters_noDelimitersProvided_returnsSingleSegment() {
        String[] args = { "alpha", "beta" };
        String[] result = ArgTools.splitByDelimiters(args);
        assertArrayEquals(new String[] { "alpha beta" }, result);
    }

    @Test
    public void splitByDelimiters_emptyArgs_returnsSingleEmptySegment() {
        String[] result = ArgTools.splitByDelimiters(new String[] {}, "/by");
        assertArrayEquals(new String[] { "" }, result);
    }

    @Test
    public void splitByDelimiters_delimiterAtStart_returnsEmptyFirstSegment() {
        String[] args = { "/by", "2026-Jan-01" };
        String[] result = ArgTools.splitByDelimiters(args, "/by");
        assertArrayEquals(new String[] { "", "2026-Jan-01" }, result);
    }

    @Test
    public void splitByDelimiters_consecutiveDelimiters_returnsEmptyMiddleSegment() {
        String[] args = { "task", "/from", "/to", "Jan", "2" };
        String[] result = ArgTools.splitByDelimiters(args, "/from", "/to");
        assertArrayEquals(new String[] { "task", "", "Jan 2" }, result);
    }

    @Test
    public void splitByDelimiters_outOfOrderDelimiter_treatedAsText() {
        String[] args = { "party", "/to", "Jan", "2" };
        String[] result = ArgTools.splitByDelimiters(args, "/from", "/to");
        assertArrayEquals(new String[] { "party /to Jan 2" }, result);
    }

    @Test
    public void splitByDelimiters_repeatedDelimiter_treatedAsTextAfterFirst() {
        String[] args = { "task", "/by", "/by", "2026" };
        String[] result = ArgTools.splitByDelimiters(args, "/by");
        assertArrayEquals(new String[] { "task", "/by 2026" }, result);
    }
}
