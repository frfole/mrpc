package com.frfole.mrpc.pack.filetree;

import org.jetbrains.annotations.NotNull;

public record ChangeEntry(@NotNull TreeNode node, ChangeType type) {
    public enum ChangeType {
        CREATE,
        MODIFY,
        DELETE
    }
}
