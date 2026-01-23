import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;

public class DatetimeConverter {
    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final List<DateTimeFormatter> INPUT_FORMATTERS = List.of(
            // Date (with year, with time)
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy[-]MMM[-]d HH:mm")
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter(Locale.ENGLISH),

            // Date (with year, no time)
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy[-]MMM[-]d")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter(Locale.ENGLISH),

            // Date (no year, with time)
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MMM[ ]d HH:mm")
                    .parseDefaulting(ChronoField.YEAR, NOW.getYear())
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter(Locale.ENGLISH),

            // Date (no year, no time)
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("MMM[ ]d")
                    .parseDefaulting(ChronoField.YEAR, NOW.getYear())
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter(Locale.ENGLISH),

            // Time only
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("HH:mm")
                    .parseDefaulting(ChronoField.YEAR, NOW.getYear())
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, NOW.getMonthValue())
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, NOW.getDayOfMonth())
                    .toFormatter(Locale.ENGLISH));

    private static final DateTimeFormatter SAME_YEAR = DateTimeFormatter.ofPattern("MMM d");
    private static final DateTimeFormatter DIFFERENT_YEAR = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter SAME_YEAR_T = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm");
    private static final DateTimeFormatter DIFFERENT_YEAR_T = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");
    private static final DateTimeFormatter SAME_DATE_T = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDateTime parse(String s) throws IbatunException {
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        throw new IbatunException("Invalid date/time: " + s);
    }

    public static String format(LocalDateTime dateTime) {
        DateTimeFormatter formatter;
        boolean includeTime = dateTime.getHour() != 0 || dateTime.getMinute() != 0 || dateTime.getSecond() != 0;
        if (dateTime.getYear() == NOW.getYear()
                && dateTime.getMonth() == NOW.getMonth()
                && dateTime.getDayOfMonth() == NOW.getDayOfMonth()) {
            formatter = SAME_DATE_T;
        } else if (dateTime.getYear() == NOW.getYear()) {
            formatter = includeTime ? SAME_YEAR_T : SAME_YEAR;
        } else {
            formatter = includeTime ? DIFFERENT_YEAR_T : DIFFERENT_YEAR;
        }
        return dateTime.format(formatter);
    }

    private DatetimeConverter() {
        // Prevent instantiation
    }
}
