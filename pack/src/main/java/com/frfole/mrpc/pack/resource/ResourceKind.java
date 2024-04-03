package com.frfole.mrpc.pack.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public enum ResourceKind {
    PROJECT_ROOT,
    DIRECTORY,
    NAMESPACE,
    PACK_META,
    PACK_ICON,
    TEXTURE,
    GENERIC_FILE;
    public static ResourceKind getType(@NotNull Path innerPath, boolean isFile) {
        String fileName = innerPath.getFileName().toString();
        int depth = innerPath.getNameCount();
        if (!isFile && depth == 1 && fileName.isEmpty()) {
            return ResourceKind.PROJECT_ROOT;
        } else if (!isFile && depth == 1) {
            return ResourceKind.NAMESPACE;
        } else if (!isFile) {
            return ResourceKind.DIRECTORY;
        } else if (depth == 1 && fileName.equals("pack.mcmeta")) {
            return ResourceKind.PACK_META;
        } else if (depth == 1 && fileName.equals("pack.png")) {
            return ResourceKind.PACK_ICON;
        } else if (depth > 2) {
            String resTypeName = innerPath.getName(1).toString();
            if (resTypeName.equals("textures") && fileName.endsWith(".png")) {
                return ResourceKind.TEXTURE;
            }
        }
        return ResourceKind.GENERIC_FILE;
    }
}
