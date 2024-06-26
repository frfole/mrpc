package com.frfole.mrpc.pack.filetree;

import com.frfole.mrpc.pack.resource.ResourceKind;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileTreeNode {
    private final Path relativePath;
    private final List<FileTreeNode> children;
    private final String name;
    private final ResourceKind kind;
    private final boolean isFile;

    public FileTreeNode(@NotNull Path path, @NotNull Path rootPath) {
        this.relativePath = rootPath.relativize(path);
        this.children = new ArrayList<>();
        this.name = path.getFileName().toString();
        this.isFile = Files.isRegularFile(path);
        this.kind = ResourceKind.getType(this.relativePath, isFile);
    }

    public @NotNull Path getRelativePath() {
        return this.relativePath;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<FileTreeNode> getChildren() {
        return this.children;
    }

    public boolean isFile() {
        return isFile;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "relativePath=" + relativePath +
                ", name=" + name +
                ", children=" + children +
                '}';
    }

    public ResourceKind getKind() {
        return this.kind;
    }
}
