package ibatun.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Resolves OS-appropriate user data locations for application files.
 */
public final class UserDataPaths {
    private UserDataPaths() {
    }

    public static Path getAppDataFile(String appName, String fileName) {
        return getAppDataDir(appName).resolve(fileName);
    }

    public static Path getAppDataDir(String appName) {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String userHome = System.getProperty("user.home");
        Path baseDir;

        if (osName.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                baseDir = Paths.get(appData);
            } else if (userHome != null && !userHome.isBlank()) {
                baseDir = Paths.get(userHome, "AppData", "Roaming");
            } else {
                baseDir = Paths.get(".");
            }
        } else if (osName.contains("mac")) {
            if (userHome != null && !userHome.isBlank()) {
                baseDir = Paths.get(userHome, "Library", "Application Support");
            } else {
                baseDir = Paths.get(".");
            }
        } else {
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (xdgDataHome != null && !xdgDataHome.isBlank()) {
                baseDir = Paths.get(xdgDataHome);
            } else if (userHome != null && !userHome.isBlank()) {
                baseDir = Paths.get(userHome, ".local", "share");
            } else {
                baseDir = Paths.get(".");
            }
        }

        return baseDir.resolve(appName);
    }
}
