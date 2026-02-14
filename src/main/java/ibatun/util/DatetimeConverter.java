package ibatun.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

import ibatun.errors.IbatunException;

/**
 * Utility class for converting between LocalDateTime objects and their string representations.
 *
 * @author Binh
 * @version 1.0
 */
public class DatetimeConverter {
    /**
     * List of input date/time formatters that do not depend on the current date.
     */
    private static final List<DateTimeFormatter> STATIC_INPUT_FORMATTERS = List
            .of(
                    // Date (with year, with time)
                    new DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("yyyy")
                            .optionalStart()
                            .appendLiteral('-')
                            .optionalEnd()
                            .optionalStart()
                            .appendLiteral(' ')
                            .optionalEnd()
                            .appendPattern("MMM")
                            .optionalStart()
                            .appendLiteral('-')
                            .optionalEnd()
                            .optionalStart()
                            .appendLiteral(' ')
                            .optionalEnd()
                            .appendPattern("d HH:mm")
                            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                            .toFormatter(Locale.ENGLISH),

                    // Date (with year, no time)
                    new DateTimeFormatterBuilder()
                            .parseCaseInsensitive()
                            .appendPattern("yyyy")
                            .optionalStart()
                            .appendLiteral('-')
                            .optionalEnd()
                            .optionalStart()
                            .appendLiteral(' ')
                            .optionalEnd()
                            .appendPattern("MMM")
                            .optionalStart()
                            .appendLiteral('-')
                            .optionalEnd()
                            .optionalStart()
                            .appendLiteral(' ')
                            .optionalEnd()
                            .appendPattern("d")
                            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                            .toFormatter(Locale.ENGLISH));

    /**
     * Formatter for dates in the same year.
     */
    private static final DateTimeFormatter SAME_YEAR = DateTimeFormatter.ofPattern("MMM d");

    /**
     * Formatter for dates in the same month.
     */
    private static final DateTimeFormatter SAME_MONTH = DateTimeFormatter.ofPattern("MMM d");

    /**
     * Formatter for dates in different years.
     */
    private static final DateTimeFormatter DIFFERENT_YEAR = DateTimeFormatter.ofPattern("MMM d, yyyy");

    /**
     * Formatter for dates and times in the same year.
     */
    private static final DateTimeFormatter SAME_YEAR_T = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm");

    /**
     * Formatter for dates and times in the same month.
     */
    private static final DateTimeFormatter SAME_MONTH_T = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm");

    /**
     * Formatter for dates and times in different years.
     */
    private static final DateTimeFormatter DIFFERENT_YEAR_T = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");

    /**
     * Formatter for times on the same date.
     */
    private static final DateTimeFormatter SAME_DATE_T = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Private constructor to prevent instantiation.
     */
    private DatetimeConverter() {
    }

    /**
     * Parses a date/time string into a LocalDateTime object.
     *
     * @param s The date/time string
     * @return The parsed LocalDateTime object
     * @throws IbatunException if the string cannot be parsed
     */
    public static LocalDateTime parse(String s) throws IbatunException {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            throw new IbatunException("Invalid date/time: " + s);
        }

        LocalDateTime isoDateTime = parseIsoDateTime(trimmed);
        if (isoDateTime != null) {
            return isoDateTime;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekday = parseWeekday(trimmed, now);
        if (weekday != null) {
            return weekday;
        }

        LocalDateTime timeOnly = parseTimeOnly(trimmed, now);
        if (timeOnly != null) {
            return timeOnly;
        }

        for (DateTimeFormatter formatter : buildInputFormatters(now)) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        throw new IbatunException("Invalid date/time: " + s);
    }

    /**
     * Formats a LocalDateTime object into a string.
     *
     * @param dateTime The LocalDateTime object
     * @return The formatted date/time string
     */
    public static String format(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        boolean includeTime = hasTimeComponent(dateTime);
        DateTimeFormatter formatter = selectFormatter(dateTime, now, includeTime);
        return dateTime.format(formatter);
    }

    private static boolean hasTimeComponent(LocalDateTime dateTime) {
        return dateTime.getHour() != 0 || dateTime.getMinute() != 0 || dateTime.getSecond() != 0;
    }

    private static DateTimeFormatter selectFormatter(LocalDateTime dateTime, LocalDateTime now, boolean includeTime) {
        if (isSameDate(dateTime, now)) {
            return SAME_DATE_T;
        } else if (isSameYearAndMonth(dateTime, now)) {
            return includeTime ? SAME_MONTH_T : SAME_MONTH;
        } else if (isSameYear(dateTime, now)) {
            return includeTime ? SAME_YEAR_T : SAME_YEAR;
        } else {
            return includeTime ? DIFFERENT_YEAR_T : DIFFERENT_YEAR;
        }
    }

    private static boolean isSameDate(LocalDateTime dateTime, LocalDateTime now) {
        return dateTime.getYear() == now.getYear()
                && dateTime.getMonth() == now.getMonth()
                && dateTime.getDayOfMonth() == now.getDayOfMonth();
    }

    private static boolean isSameYearAndMonth(LocalDateTime dateTime, LocalDateTime now) {
        return dateTime.getYear() == now.getYear() && dateTime.getMonth() == now.getMonth();
    }

    private static boolean isSameYear(LocalDateTime dateTime, LocalDateTime now) {
        return dateTime.getYear() == now.getYear();
    }

    private static List<DateTimeFormatter> buildInputFormatters(LocalDateTime now) {
        return List
                .of(STATIC_INPUT_FORMATTERS.get(0), STATIC_INPUT_FORMATTERS.get(1), buildMonthDayYearFormatter(),
                        buildDayMonthYearFormatter(), buildNumericDayFirstFormatter(),
                        buildNumericDayFirstDashFormatter(), buildNoYearWithTimeFormatter(now),
                        buildNoYearWithTime12Formatter(now), buildNoYearNoTimeFormatter(now),
                        buildTimeOnly24Formatter(now));
    }

    private static DateTimeFormatter buildMonthDayYearFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM")
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("d")
                .optionalStart()
                .appendLiteral(',')
                .optionalEnd()
                .appendLiteral(' ')
                .appendPattern("yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildDayMonthYearFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("MMM")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildNumericDayFirstFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d/M/yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildNumericDayFirstDashFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d-M-yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildNoYearWithTimeFormatter(LocalDateTime now) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("d HH:mm")
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildNoYearNoTimeFormatter(LocalDateTime now) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("d")
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildNoYearWithTime12Formatter(LocalDateTime now) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("MMM")
                .optionalStart()
                .appendLiteral('-')
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("d ")
                .appendPattern("h")
                .optionalStart()
                .appendPattern(":mm")
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("a")
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildTimeOnly24Formatter(LocalDateTime now) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("HH:mm")
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, now.getMonthValue())
                .parseDefaulting(ChronoField.DAY_OF_MONTH, now.getDayOfMonth())
                .toFormatter(Locale.ENGLISH);
    }

    private static LocalDateTime parseIsoDateTime(String input) {
        try {
            return OffsetDateTime.parse(input).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // Try next format
        }

        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Try next format
        }

        try {
            return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDateTime parseTimeOnly(String input, LocalDateTime now) {
        List<DateTimeFormatter> formatters = List.of(buildTime12Formatter(), buildTime12CompactFormatter());

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalTime parsed = LocalTime.parse(input, formatter);
                return adjustToFutureTime(now, parsed);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        return null;
    }

    private static DateTimeFormatter buildTime12Formatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h")
                .optionalStart()
                .appendPattern(":mm")
                .optionalEnd()
                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()
                .appendPattern("a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static DateTimeFormatter buildTime12CompactFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h")
                .optionalStart()
                .appendPattern(":mm")
                .optionalEnd()
                .appendPattern("a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter(Locale.ENGLISH);
    }

    private static LocalDateTime adjustToFutureTime(LocalDateTime now, LocalTime parsed) {
        LocalDateTime candidate = now
                .withHour(parsed.getHour())
                .withMinute(parsed.getMinute())
                .withSecond(0)
                .withNano(0);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private static LocalDateTime parseWeekday(String input, LocalDateTime now) {
        String lower = input.trim().toLowerCase(Locale.ENGLISH);
        java.time.DayOfWeek target = getTargetDayOfWeek(lower);

        if (target == null) {
            return null;
        }

        return calculateNextOccurrence(now, target);
    }

    private static java.time.DayOfWeek getTargetDayOfWeek(String lower) {
        if (lower.equals("mon") || lower.equals("monday")) {
            return java.time.DayOfWeek.MONDAY;
        } else if (lower.equals("tue") || lower.equals("tues") || lower.equals("tuesday")) {
            return java.time.DayOfWeek.TUESDAY;
        } else if (lower.equals("wed") || lower.equals("weds") || lower.equals("wednesday")) {
            return java.time.DayOfWeek.WEDNESDAY;
        } else if (lower.equals("thu") || lower.equals("thur") || lower.equals("thurs") || lower.equals("thursday")) {
            return java.time.DayOfWeek.THURSDAY;
        } else if (lower.equals("fri") || lower.equals("friday")) {
            return java.time.DayOfWeek.FRIDAY;
        } else if (lower.equals("sat") || lower.equals("saturday")) {
            return java.time.DayOfWeek.SATURDAY;
        } else if (lower.equals("sun") || lower.equals("sunday")) {
            return java.time.DayOfWeek.SUNDAY;
        }
        return null;
    }

    private static LocalDateTime calculateNextOccurrence(LocalDateTime now, java.time.DayOfWeek target) {
        int today = now.getDayOfWeek().getValue();
        int desired = target.getValue();
        int daysUntil = (desired - today + 7) % 7;
        if (daysUntil == 0) {
            daysUntil = 7;
        }
        return now.plusDays(daysUntil).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
