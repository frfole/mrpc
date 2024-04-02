package com.frfole.mrpc.pack.filetree;

import com.frfole.mrpc.pack.resource.ResourceKind;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private final Path path;
    private final List<TreeNode> children;
    private final String name;
    private final ResourceKind kind;
    private final boolean isFile;

    public TreeNode(@NotNull Path path, @NotNull Path rootPath) {
        this.path = path;
        this.children = new ArrayList<>();
        this.name = path.getFileName().toString();
        this.isFile = Files.isRegularFile(path);
        this.kind = ResourceKind.getType(rootPath.relativize(path), isFile);
    }

    public @NotNull Path getPath() {
        return this.path;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<TreeNode> getChildren() {
        return this.children;
    }

    public boolean isFile() {
        return isFile;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "path=" + path +
                ", name=" + name +
                ", children=" + children +
                '}';
    }

    public ResourceKind getKind() {
        return this.kind;
    }
}
