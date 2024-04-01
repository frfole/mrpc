package com.frfole.mrpc.app.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public final class ConfigUtils {
    // contains the path to the config directory of the application
    public static final Path configRoot;
    static {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("mac") || osName.contains("darwin")) {
            String userHome = System.getenv("HOME");
            if (userHome == null) {
                userHome = System.getProperty("user.dir");
            }
            configRoot = Paths.get(userHome).resolve("Library").resolve("Application Support").resolve("mrpc");
        } else if (osName.contains("win")) {
            configRoot = Paths.get(System.getenv("LOCALAPPDATA")).resolve("mrpc");
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            String userHome = System.getenv("XDG_CONFIG_HOME");
            if (userHome == null) {
                userHome = System.getenv("HOME");
                if (userHome == null) {
                    userHome = System.getProperty("user.dir");
                }
                configRoot = Paths.get(userHome).resolve(".config").resolve("mrpc");
            } else {
                configRoot = Paths.get(userHome).resolve("mrpc");
            }
        } else {
            configRoot = Paths.get(System.getProperty("user.dir")).resolve("mrpc");
        }
    }
}
