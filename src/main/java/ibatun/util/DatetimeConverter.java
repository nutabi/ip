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
     * The list of input date/time formatters that do not depend on the current date.
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
     * The formatter for dates in the same year.
     */
    private static final DateTimeFormatter SAME_YEAR = DateTimeFormatter.ofPattern("MMM d");

    /**
     * The formatter for dates in the same month.
     */
    private static final DateTimeFormatter SAME_MONTH = DateTimeFormatter.ofPattern("MMM d");

    /**
     * The formatter for dates in different years.
     */
    private static final DateTimeFormatter DIFFERENT_YEAR = DateTimeFormatter.ofPattern("MMM d, yyyy");

    /**
     * The formatter for dates and times in the same year.
     */
    private static final DateTimeFormatter SAME_YEAR_T = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm");

    /**
     * The formatter for dates and times in the same month.
     */
    private static final DateTimeFormatter SAME_MONTH_T = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm");

    /**
     * The formatter for dates and times in different years.
     */
    private static final DateTimeFormatter DIFFERENT_YEAR_T = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");

    /**
     * The formatter for times on the same date.
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
        DateTimeFormatter formatter;
        boolean includeTime = dateTime.getHour() != 0 || dateTime.getMinute() != 0 || dateTime.getSecond() != 0;
        if (dateTime.getYear() == now.getYear()
                && dateTime.getMonth() == now.getMonth()
                && dateTime.getDayOfMonth() == now.getDayOfMonth()) {
            formatter = SAME_DATE_T;
        } else if (dateTime.getYear() == now.getYear() && dateTime.getMonth() == now.getMonth()) {
            formatter = includeTime ? SAME_MONTH_T : SAME_MONTH;
        } else if (dateTime.getYear() == now.getYear()) {
            formatter = includeTime ? SAME_YEAR_T : SAME_YEAR;
        } else {
            formatter = includeTime ? DIFFERENT_YEAR_T : DIFFERENT_YEAR;
        }
        return dateTime.format(formatter);
    }

    private static List<DateTimeFormatter> buildInputFormatters(LocalDateTime now) {
        DateTimeFormatter monthDayYear = new DateTimeFormatterBuilder()
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

        DateTimeFormatter dayMonthYear = new DateTimeFormatterBuilder()
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

        DateTimeFormatter numericDayFirst = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d/M/yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);

        DateTimeFormatter numericDayFirstDash = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("d-M-yyyy")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);

        DateTimeFormatter noYearWithTime = new DateTimeFormatterBuilder()
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

        DateTimeFormatter noYearNoTime = new DateTimeFormatterBuilder()
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

        DateTimeFormatter noYearWithTime12 = new DateTimeFormatterBuilder()
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

        DateTimeFormatter timeOnly24 = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("HH:mm")
                .parseDefaulting(ChronoField.YEAR, now.getYear())
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, now.getMonthValue())
                .parseDefaulting(ChronoField.DAY_OF_MONTH, now.getDayOfMonth())
                .toFormatter(Locale.ENGLISH);

        return List
                .of(STATIC_INPUT_FORMATTERS.get(0), STATIC_INPUT_FORMATTERS.get(1), monthDayYear, dayMonthYear,
                        numericDayFirst, numericDayFirstDash, noYearWithTime, noYearWithTime12, noYearNoTime,
                        timeOnly24);
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
        DateTimeFormatter time12 = new DateTimeFormatterBuilder()
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

        DateTimeFormatter time12Compact = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h")
                .optionalStart()
                .appendPattern(":mm")
                .optionalEnd()
                .appendPattern("a")
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter(Locale.ENGLISH);

        for (DateTimeFormatter formatter : List.of(time12, time12Compact)) {
            try {
                LocalTime parsed = LocalTime.parse(input, formatter);
                LocalDateTime candidate = now
                        .withHour(parsed.getHour())
                        .withMinute(parsed.getMinute())
                        .withSecond(0)
                        .withNano(0);
                if (!candidate.isAfter(now)) {
                    candidate = candidate.plusDays(1);
                }
                return candidate;
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        return null;
    }

    private static LocalDateTime parseWeekday(String input, LocalDateTime now) {
        String lower = input.trim().toLowerCase(Locale.ENGLISH);
        java.time.DayOfWeek target = null;
        if (lower.equals("mon") || lower.equals("monday")) {
            target = java.time.DayOfWeek.MONDAY;
        } else if (lower.equals("tue") || lower.equals("tues") || lower.equals("tuesday")) {
            target = java.time.DayOfWeek.TUESDAY;
        } else if (lower.equals("wed") || lower.equals("weds") || lower.equals("wednesday")) {
            target = java.time.DayOfWeek.WEDNESDAY;
        } else if (lower.equals("thu") || lower.equals("thur") || lower.equals("thurs") || lower.equals("thursday")) {
            target = java.time.DayOfWeek.THURSDAY;
        } else if (lower.equals("fri") || lower.equals("friday")) {
            target = java.time.DayOfWeek.FRIDAY;
        } else if (lower.equals("sat") || lower.equals("saturday")) {
            target = java.time.DayOfWeek.SATURDAY;
        } else if (lower.equals("sun") || lower.equals("sunday")) {
            target = java.time.DayOfWeek.SUNDAY;
        }

        if (target == null) {
            return null;
        }

        int today = now.getDayOfWeek().getValue();
        int desired = target.getValue();
        int daysUntil = (desired - today + 7) % 7;
        if (daysUntil == 0) {
            daysUntil = 7;
        }
        return now.plusDays(daysUntil).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
