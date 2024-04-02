package com.frfole.mrpc.pack.filetree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileTree {
    private final TreeNode rootNode;
    private final Set<WatchKey> watchKeys = new HashSet<>();

    public FileTree(Path rootPath) {
        this.rootNode = new TreeNode(rootPath, rootPath);
    }

    public void build() throws IOException {
        for (WatchKey key : this.watchKeys) {
            key.cancel();
        }
        this.watchKeys.clear();
        buildNode(this.rootNode, 100);
    }

    public void close() {
        for (WatchKey key : watchKeys) {
            key.cancel();
        }
        watchKeys.clear();
    }

    public void checkChanges(@NotNull Consumer<ChangeEntry> changeConsumer) throws IOException {
        watchKeys.removeIf(key -> !key.isValid());
        for (WatchKey key : Set.copyOf(watchKeys)) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event == null || !(key.watchable() instanceof Path parentPath)) continue;
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    // get file parent node
                    TreeNode parentNode = getNode(parentPath);
                    if (parentNode == null) {
                        throw new RuntimeException("failed to find tree node for: " + parentPath);
                    }
                    // check if the file already existed, if so remove it
                    String childName = ((Path) event.context()).getFileName().toString();
                    for (int i = 0; i < parentNode.getChildren().size(); i++) {
                        TreeNode childNode = parentNode.getChildren().get(i);
                        if (childNode.getName().equals(childName)) {
                            parentNode.getChildren().remove(i);
                            changeConsumer.accept(new ChangeEntry(childNode, ChangeEntry.ChangeType.DELETE));
                            break;
                        }
                    }

                    // create node for the new file
                    Path childPath = parentPath.resolve((Path) event.context());
                    TreeNode childNode = new TreeNode(childPath, rootNode.getPath());
                    parentNode.getChildren().add(childNode);
                    parentNode.getChildren().sort(FileTree::sort);
                    buildNode(childNode, 100);
                    changeConsumer.accept(new ChangeEntry(childNode, ChangeEntry.ChangeType.CREATE));
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path childPath = parentPath.resolve((Path) event.context());
                    TreeNode node = getNode(childPath);
                    if (node == null) {
                        throw new RuntimeException("failed to find tree node for: " + childPath);
                    }
                    changeConsumer.accept(new ChangeEntry(node, ChangeEntry.ChangeType.MODIFY));
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    TreeNode parentNode = getNode(parentPath);
                    if (parentNode == null) {
                        throw new RuntimeException("failed to find tree node for: " + parentPath);
                    }
                    String childName = ((Path) event.context()).getFileName().toString();
                    for (int i = 0; i < parentNode.getChildren().size(); i++) {
                        TreeNode childNode = parentNode.getChildren().get(i);
                        if (childNode.getName().equals(childName)) {
                            parentNode.getChildren().remove(i);
                            changeConsumer.accept(new ChangeEntry(childNode, ChangeEntry.ChangeType.DELETE));
                            break;
                        }
                    }
                }
            }
        }
    }

    private void buildNode(@NotNull TreeNode node, int inverseDepth) throws IOException {
        if (!Files.isDirectory(node.getPath())) {
            return;
        } else {
            watchKeys.add(node.getPath().register(FileSystems.getDefault().newWatchService(), StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY));
        }
        node.getChildren().clear();
        if (inverseDepth < 0) {
            return;
        }
        try (Stream<Path> subPaths = Files.list(node.getPath())) {
            Iterator<Path> iterator = subPaths.iterator();
            while (iterator.hasNext()) {
                TreeNode subNode = new TreeNode(iterator.next(), rootNode.getPath());
                node.getChildren().add(subNode);
                buildNode(subNode, inverseDepth - 1);
            }
        }
        node.getChildren().sort(FileTree::sort);
    }

    private @Nullable TreeNode getNode(@NotNull Path path) {
        if (path.equals(rootNode.getPath())) return rootNode;
        Iterator<Path> iterator = this.rootNode.getPath().relativize(path).iterator();
        TreeNode tempNode = rootNode;
        boolean found;
        while (iterator.hasNext()) {
            found = false;
            Path next = iterator.next();
            for (TreeNode child : tempNode.getChildren()) {
                if (child.getName().equals(next.toString())) {
                    tempNode = child;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return tempNode;
    }

    private static int sort(TreeNode left, TreeNode right) {
        if (left.isFile() && !right.isFile()) return 1;
        else if (!left.isFile() && right.isFile()) return -1;
        else return left.getName().compareTo(right.getName());
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
}
