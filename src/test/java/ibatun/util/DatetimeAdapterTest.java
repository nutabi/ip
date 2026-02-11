package ibatun.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.format.DateTimeParseException;

public class DatetimeAdapterTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new DatetimeAdapter())
            .create();

    @Test
    public void serializeDeserialize_roundTrip_success() {
        LocalDateTime input = LocalDateTime.of(2026, 2, 11, 9, 45, 30);
        String json = gson.toJson(input, LocalDateTime.class);
        LocalDateTime output = gson.fromJson(json, LocalDateTime.class);
        assertEquals(input, output);
    }

    @Test
    public void serialize_formatsAsIsoLocalDateTime() {
        LocalDateTime input = LocalDateTime.of(2026, 2, 11, 9, 45, 30);
        String json = gson.toJson(input, LocalDateTime.class);
        assertEquals("\"2026-02-11T09:45:30\"", json);
    }

    @Test
    public void deserialize_null_returnsNull() {
        LocalDateTime output = gson.fromJson("null", LocalDateTime.class);
        assertNull(output);
    }

    @Test
    public void deserialize_invalidFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> gson.fromJson("\"not-a-date\"", LocalDateTime.class));
    }

    @Test
    public void deserialize_emptyString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> gson.fromJson("\"\"", LocalDateTime.class));
    }
}
