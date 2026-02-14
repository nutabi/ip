package ibatun.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class UserDataPathsTest {
    @Test
    public void getAppDataDir_isReadableAndWritable() throws Exception {
        Path appDir = UserDataPaths.getAppDataDir("IbatunTest");
        assertNotNull(appDir);
        Files.createDirectories(appDir);

        Path probeFile = appDir.resolve("permissions-test.txt");
        Files.writeString(probeFile, "ok");

        assertTrue(Files.isReadable(appDir));
        assertTrue(Files.isWritable(appDir));
        assertTrue(Files.isReadable(probeFile));
        assertTrue(Files.isWritable(probeFile));

        Files.deleteIfExists(probeFile);
    }

    @Test
    public void getAppDataFile_usesAppNameAndFileName() throws Exception {
        Path filePath = UserDataPaths.getAppDataFile("IbatunTest", "tasks.json");
        assertNotNull(filePath);
        assertEquals("tasks.json", filePath.getFileName().toString());
        assertEquals("IbatunTest", filePath.getParent().getFileName().toString());

        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "ok");
        assertTrue(Files.isReadable(filePath));
        assertTrue(Files.isWritable(filePath));

        Files.deleteIfExists(filePath);
    }
}
