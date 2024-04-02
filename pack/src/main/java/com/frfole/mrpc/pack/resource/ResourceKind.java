package com.frfole.mrpc.pack.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public enum ResourceKind {
    PROJECT_ROOT,
    DIRECTORY,
    NAMESPACE,
    PACK_META,
    PACK_ICON,
    GENERIC_FILE;
    public static ResourceKind getType(@NotNull Path innerPath, boolean isFile) {
        String part = innerPath.getFileName().toString();
        int depth = innerPath.getNameCount();
        if (!isFile && depth == 1 && part.isEmpty()) {
            return ResourceKind.PROJECT_ROOT;
        } else if (!isFile && depth == 1) {
            return ResourceKind.NAMESPACE;
        } else if (!isFile) {
            return ResourceKind.DIRECTORY;
        } else if (depth == 1 && part.equals("pack.mcmeta")) {
            return ResourceKind.PACK_META;
        } else if (depth == 1 && part.equals("pack.png")) {
            return ResourceKind.PACK_ICON;
        }else {
            return ResourceKind.GENERIC_FILE;
        }
    }
}
