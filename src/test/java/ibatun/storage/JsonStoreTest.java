package ibatun.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ibatun.errors.IbatunCorruptedDataException;
import ibatun.errors.IbatunException;
import ibatun.tasks.Task;
import ibatun.tasks.Todo;

public class JsonStoreTest {
    @TempDir
    Path tempDir;

    @Test
    public void constructor_missingFile_startsEmpty() throws Exception {
        Path filePath = tempDir.resolve("missing.json");
        JsonStore store = new JsonStore(filePath.toString());
        assertEquals(0, store.list().size());
    }

    @Test
    public void constructor_emptyFile_startsEmpty() throws Exception {
        Path filePath = tempDir.resolve("empty.json");
        Files.writeString(filePath, "");
        JsonStore store = new JsonStore(filePath.toString());
        assertEquals(0, store.list().size());
    }

    @Test
    public void add_writesFile() throws Exception {
        Path filePath = tempDir.resolve("tasks.json");
        JsonStore store = new JsonStore(filePath.toString());
        store.add(new Todo("read"));

        assertTrue(Files.exists(filePath));
        String content = Files.readString(filePath);
        assertFalse(content.isBlank());
        assertTrue(content.contains("\"type\":\"todo\""));
    }

    @Test
    public void remove_updatesFileToEmptyList() throws Exception {
        Path filePath = tempDir.resolve("tasks.json");
        JsonStore store = new JsonStore(filePath.toString());
        store.add(new Todo("read"));
        store.remove(0);

        String content = Files.readString(filePath);
        assertEquals("[]", content);
    }

    @Test
    public void modify_updatesFileContents() throws Exception {
        Path filePath = tempDir.resolve("tasks.json");
        JsonStore store = new JsonStore(filePath.toString());
        store.add(new Todo("read"));
        store.modify(0, Task::mark);

        String content = Files.readString(filePath);
        assertTrue(content.contains("\"isDone\":true"));
    }

    @Test
    public void constructor_invalidPath_throws() {
        assertThrows(IbatunException.class, () -> new JsonStore("\0"));
    }

    @Test
    public void constructor_corruptedData_throwsWarningException() throws Exception {
        Path filePath = tempDir.resolve("corrupted.json");
        Files.writeString(filePath, "{not valid json");

        assertThrows(IbatunCorruptedDataException.class, () -> new JsonStore(filePath.toString()));
    }

    @Test
    public void constructor_corruptedData_recoverOverwritesOnSave() throws Exception {
        Path filePath = tempDir.resolve("corrupted.json");
        Files.writeString(filePath, "{not valid json");

        JsonStore store = new JsonStore(filePath.toString(), true);
        assertEquals(0, store.list().size());

        store.add(new Todo("read"));
        String content = Files.readString(filePath);
        assertTrue(content.contains("\"type\":\"todo\""));
        assertFalse(content.contains("{not valid json"));
    }
}
