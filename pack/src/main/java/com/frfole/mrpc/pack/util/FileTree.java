package com.frfole.mrpc.pack.util;

import com.frfole.mrpc.pack.resource.ResourceKind;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class FileTree {
    private final TreeNode rootNode;

    public FileTree(Path rootPath) {
        this.rootNode = new TreeNode(rootPath, rootPath);
    }

    public void build() throws IOException {
        buildNode(this.rootNode, 100);
    }

    private void buildNode(@NotNull TreeNode node, int inverseDepth) throws IOException {
        if (!Files.isDirectory(node.path)) {
            return;
        }
        node.children.clear();
        if (inverseDepth < 0) {
            return;
        }
        try (Stream<Path> subPaths = Files.list(node.path)) {
            Iterator<Path> iterator = subPaths.iterator();
            while (iterator.hasNext()) {
                TreeNode subNode = new TreeNode(iterator.next(), rootNode.path);
                node.children.add(subNode);
                buildNode(subNode, inverseDepth - 1);
            }
        }
        node.children.sort(FileTree::sort);
    }

    private static int sort(TreeNode left, TreeNode right) {
        if (left.isFile && !right.isFile) return 1;
        else if (!left.isFile && right.isFile) return -1;
        else return left.name.compareTo(right.name);
    }

    @Override
    public String toString() {
        return "FileTree{" +
                "rootNode=" + rootNode +
                '}';
    }

    public @NotNull TreeNode getRootNode() {
        return this.rootNode;
    }

    public static class TreeNode {
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
}
