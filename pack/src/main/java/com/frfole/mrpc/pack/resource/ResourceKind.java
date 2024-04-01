package com.frfole.mrpc.pack.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Iterator;

public enum ResourceKind {
    PROJECT_ROOT,
    DIRECTORY,
    NAMESPACE,
    PACK_META,
    GENERIC_FILE;
    public static ResourceKind getType(@NotNull Path innerPath, boolean isFile) {
        Iterator<Path> parts = innerPath.iterator();
        String part = "";
        int depth = 0;
        while (parts.hasNext()) {
            depth++;
            part = parts.next().toString();
        }
        if (!isFile && depth == 1 && part.isEmpty()) {
            return ResourceKind.PROJECT_ROOT;
        } else if (!isFile && depth == 1) {
            return ResourceKind.NAMESPACE;
        } else if (!isFile) {
            return ResourceKind.DIRECTORY;
        } else if (depth == 1 && part.equals("pack.mcmeta")) {
            return ResourceKind.PACK_META;
        } else {
            return ResourceKind.GENERIC_FILE;
        }
    }
}
