package ibatun.util;

import static ibatun.util.DatetimeConverter.format;
import static ibatun.util.DatetimeConverter.parse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class DatetimeConverterTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    public void parse_dateWithYearWithTime_success() {
        var input1 = "2024-Mar-15 14:30";
        var dt = assertDoesNotThrow(() -> parse(input1));
        assertEquals(dt.getYear(), 2024);
        assertEquals(dt.getMonthValue(), 3);
        assertEquals(dt.getDayOfMonth(), 15);
        assertEquals(dt.getHour(), 14);
        assertEquals(dt.getMinute(), 30);

        var input2 = "2026Jan06 12:15";
        dt = assertDoesNotThrow(() -> parse(input2));
        assertEquals(dt.getYear(), 2026);
        assertEquals(dt.getMonthValue(), 1);
        assertEquals(dt.getDayOfMonth(), 6);
        assertEquals(dt.getHour(), 12);
        assertEquals(dt.getMinute(), 15);
    }

    @Test
    public void parse_dateWithYearNoTime_success() {
        var input1 = "2024-Mar-15";
        var dt = assertDoesNotThrow(() -> parse(input1));
        assertEquals(dt.getYear(), 2024);
        assertEquals(dt.getMonthValue(), 3);
        assertEquals(dt.getDayOfMonth(), 15);
        assertEquals(dt.getHour(), 0);
        assertEquals(dt.getMinute(), 0);

        var input2 = "2026Jan06";
        dt = assertDoesNotThrow(() -> parse(input2));
        assertEquals(dt.getYear(), 2026);
        assertEquals(dt.getMonthValue(), 1);
        assertEquals(dt.getDayOfMonth(), 6);
        assertEquals(dt.getHour(), 0);
        assertEquals(dt.getMinute(), 0);
    }

    @Test
    public void parse_dateNoYearWithTime_success() {
        var input1 = "Mar-15 14:30";
        var dt = assertDoesNotThrow(() -> parse(input1));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), 3);
        assertEquals(dt.getDayOfMonth(), 15);
        assertEquals(dt.getHour(), 14);
        assertEquals(dt.getMinute(), 30);

        var input2 = "Jan06 12:15";
        dt = assertDoesNotThrow(() -> parse(input2));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), 1);
        assertEquals(dt.getDayOfMonth(), 6);
        assertEquals(dt.getHour(), 12);
        assertEquals(dt.getMinute(), 15);
    }

    @Test
    public void parse_dateNoYearNoTime_success() {
        var input1 = "Mar-15";
        var dt = assertDoesNotThrow(() -> parse(input1));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), 3);
        assertEquals(dt.getDayOfMonth(), 15);
        assertEquals(dt.getHour(), 0);
        assertEquals(dt.getMinute(), 0);

        var input2 = "Jan06";
        dt = assertDoesNotThrow(() -> parse(input2));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), 1);
        assertEquals(dt.getDayOfMonth(), 6);
        assertEquals(dt.getHour(), 0);
        assertEquals(dt.getMinute(), 0);
    }

    @Test
    public void parse_timeOnly_success() {
        var input1 = "14:30";
        var dt = assertDoesNotThrow(() -> parse(input1));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), NOW.getMonthValue());
        assertEquals(dt.getDayOfMonth(), NOW.getDayOfMonth());
        assertEquals(dt.getHour(), 14);
        assertEquals(dt.getMinute(), 30);

        var input2 = "09:15";
        dt = assertDoesNotThrow(() -> parse(input2));
        assertEquals(dt.getYear(), NOW.getYear());
        assertEquals(dt.getMonthValue(), NOW.getMonthValue());
        assertEquals(dt.getDayOfMonth(), NOW.getDayOfMonth());
        assertEquals(dt.getHour(), 9);
        assertEquals(dt.getMinute(), 15);
    }

    @Test
    public void parse_timeOnly12h_success() {
        LocalDateTime now = LocalDateTime.now();
        var dt = assertDoesNotThrow(() -> parse("2pm"));
        var expected = expectedTimeOnly(now, LocalTime.of(14, 0));
        assertEquals(expected.getYear(), dt.getYear());
        assertEquals(expected.getMonthValue(), dt.getMonthValue());
        assertEquals(expected.getDayOfMonth(), dt.getDayOfMonth());
        assertEquals(expected.getHour(), dt.getHour());
        assertEquals(expected.getMinute(), dt.getMinute());
    }

    @Test
    public void parse_weekday_success() {
        LocalDateTime now = LocalDateTime.now();
        var dt = assertDoesNotThrow(() -> parse("mon"));
        var expected = expectedWeekday(now, DayOfWeek.MONDAY);
        assertEquals(expected.getYear(), dt.getYear());
        assertEquals(expected.getMonthValue(), dt.getMonthValue());
        assertEquals(expected.getDayOfMonth(), dt.getDayOfMonth());
        assertEquals(expected.getHour(), dt.getHour());
        assertEquals(expected.getMinute(), dt.getMinute());
    }

    @Test
    public void parse_invalidInput_throws() {
        String[] invalidStrings = { "15-Mar-2024", // Wrong format
            "2024/03/15", // Wrong format
            "March 15, 2024", // Wrong format
            "14:60", // Invalid minute
            "25:00", // Invalid hour
            "2024-Mar-32", // Invalid day
            "abcd-ef-gh" // Nonsense
        };
        for (String input : invalidStrings) {
            assertThrows(Exception.class, () -> parse(input));
        }
    }

    @Test
    public void format_sameYearNoTime_success() {
        var dt = LocalDateTime.of(NOW.getYear(), 5, 10, 0, 0);
        var formatted = format(dt);
        assertEquals("May 10", formatted);
    }

    @Test
    public void format_sameMonthNoTime_success() {
        int day = NOW.getDayOfMonth() == 1 ? 2 : 1;
        var dt = LocalDateTime.of(NOW.getYear(), NOW.getMonthValue(), day, 0, 0);
        var formatted = format(dt);
        assertEquals(String.valueOf(day), formatted);
    }

    @Test
    public void format_differentYearNoTime_success() {
        var dt = LocalDateTime.of(NOW.getYear() + 1, 5, 10, 0, 0);
        var formatted = format(dt);
        assertEquals("May 10, " + (NOW.getYear() + 1), formatted);
    }

    @Test
    public void format_sameYearWithTime_success() {
        var dt = LocalDateTime.of(NOW.getYear(), 5, 10, 14, 30);
        var formatted = format(dt);
        assertEquals("May 10 at 14:30", formatted);
    }

    @Test
    public void format_sameMonthWithTime_success() {
        int day = NOW.getDayOfMonth() == 1 ? 2 : 1;
        var dt = LocalDateTime.of(NOW.getYear(), NOW.getMonthValue(), day, 14, 30);
        var formatted = format(dt);
        assertEquals(day + " at 14:30", formatted);
    }

    @Test
    public void format_differentYearWithTime_success() {
        var dt = LocalDateTime.of(NOW.getYear() + 1, 5, 10, 14, 30);
        var formatted = format(dt);
        assertEquals("May 10, " + (NOW.getYear() + 1) + " at 14:30", formatted);
    }

    @Test
    public void format_sameDateTimeOnly_success() {
        var dt = LocalDateTime.of(NOW.getYear(), NOW.getMonthValue(), NOW.getDayOfMonth(), 9, 15);
        var formatted = format(dt);
        assertEquals("09:15", formatted);
    }

    private static LocalDateTime expectedTimeOnly(LocalDateTime now, LocalTime time) {
        LocalDateTime candidate = now.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(0).withNano(0);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private static LocalDateTime expectedWeekday(LocalDateTime now, DayOfWeek target) {
        int today = now.getDayOfWeek().getValue();
        int desired = target.getValue();
        int daysUntil = (desired - today + 7) % 7;
        if (daysUntil == 0) {
            daysUntil = 7;
        }
        return now.plusDays(daysUntil).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
