package ibatun.util;

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
}
