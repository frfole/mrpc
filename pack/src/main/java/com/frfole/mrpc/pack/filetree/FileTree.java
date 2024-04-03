package com.frfole.mrpc.pack.filetree;

import com.frfole.mrpc.pack.resource.ResourceKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileTree {
    private final Path rootPath;
    private final FileTreeNode rootNode;
    private final Set<WatchKey> watchKeys = new HashSet<>();
    public final Set<FileTreeNode> textures = new HashSet<>();
    private final WatchService watchService;

    public FileTree(Path rootPath) {
        this.rootPath = rootPath;
        this.rootNode = new FileTreeNode(rootPath, rootPath);
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void build() throws IOException {
        clean();
        buildNode(this.rootNode, 100);
    }

    public void clean() {
        for (WatchKey key : watchKeys) {
            key.cancel();
        }
        watchKeys.clear();
        this.textures.clear();
    }

    public void checkChanges(@NotNull Consumer<ChangeEntry> changeConsumer) throws IOException {
        watchKeys.removeIf(key -> !key.isValid());
        for (WatchKey key : Set.copyOf(watchKeys)) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event == null || !(key.watchable() instanceof Path parentPath)) continue;
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    // get file parent node
                    FileTreeNode parentNode = getNode(parentPath);
                    if (parentNode == null) {
                        throw new RuntimeException("failed to find tree node for: " + parentPath);
                    }
                    // check if the file already existed, if so remove it
                    String childName = ((Path) event.context()).getFileName().toString();
                    for (int i = 0; i < parentNode.getChildren().size(); i++) {
                        FileTreeNode childNode = parentNode.getChildren().get(i);
                        if (childNode.getName().equals(childName)) {
                            parentNode.getChildren().remove(i);
                            changeConsumer.accept(new ChangeEntry(childNode, ChangeEntry.ChangeType.DELETE));
                            break;
                        }
                    }

                    // create node for the new file
                    Path childPath = parentPath.resolve((Path) event.context());
                    FileTreeNode childNode = new FileTreeNode(childPath, rootPath);
                    parentNode.getChildren().add(childNode);
                    parentNode.getChildren().sort(FileTree::sort);
                    buildNode(childNode, 100);
                    changeConsumer.accept(new ChangeEntry(childNode, ChangeEntry.ChangeType.CREATE));
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path childPath = parentPath.resolve((Path) event.context());
                    FileTreeNode node = getNode(childPath);
                    if (node == null) {
                        throw new RuntimeException("failed to find tree node for: " + childPath);
                    }
                    changeConsumer.accept(new ChangeEntry(node, ChangeEntry.ChangeType.MODIFY));
                } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    FileTreeNode parentNode = getNode(parentPath);
                    if (parentNode == null) {
                        throw new RuntimeException("failed to find tree node for: " + parentPath);
                    }
                    String childName = ((Path) event.context()).getFileName().toString();
                    for (int i = 0; i < parentNode.getChildren().size(); i++) {
                        FileTreeNode childNode = parentNode.getChildren().get(i);
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

    private void buildNode(@NotNull FileTreeNode node, int inverseDepth) throws IOException {
        Path absolutePath = rootPath.resolve(node.getRelativePath());
        if (!Files.isDirectory(absolutePath)) {
            if (node.getKind() == ResourceKind.TEXTURE) {
                textures.add(node);
            }
            return;
        } else {
            watchKeys.add(absolutePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY));
        }
        node.getChildren().clear();
        if (inverseDepth < 0) {
            return;
        }
        try (Stream<Path> subPaths = Files.list(absolutePath)) {
            Iterator<Path> iterator = subPaths.iterator();
            while (iterator.hasNext()) {
                FileTreeNode subNode = new FileTreeNode(iterator.next(), rootPath);
                node.getChildren().add(subNode);
                buildNode(subNode, inverseDepth - 1);
            }
        }
        node.getChildren().sort(FileTree::sort);
    }

    /**
     * Get the tree node that is associated with given path. If the node does not exist null is returned.
     * @param path the absolute path of the node
     * @return associated tree node
     */
    private @Nullable FileTreeNode getNode(@NotNull Path path) {
        if (path.equals(rootPath)) return rootNode;
        Iterator<Path> iterator = this.rootPath.relativize(path).iterator();
        FileTreeNode tempNode = rootNode;
        boolean found;
        while (iterator.hasNext()) {
            found = false;
            Path next = iterator.next();
            for (FileTreeNode child : tempNode.getChildren()) {
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

    private static int sort(FileTreeNode left, FileTreeNode right) {
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

    public @NotNull FileTreeNode getRootNode() {
        return this.rootNode;
    }
}
