package com.frfole.mrpc.pack;

import com.frfole.mrpc.pack.meta.PackMeta;
import com.frfole.mrpc.pack.filetree.FileTree;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public class Project {
    private final Path projectRoot;
    private final Gson gson;
    private final PackMeta meta;
    private final FileTree fileTree;

    public Project(@NotNull Path projectRoot, @NotNull Gson gson) {
        this.projectRoot = projectRoot;
        this.fileTree = new FileTree(projectRoot);
        this.gson = gson;
        this.meta = new PackMeta(this.projectRoot.resolve("pack.mcmeta"));
    }

    public Project(@NotNull Path projectRoot) {
        this(projectRoot, new Gson());
    }

    public @NotNull Path getProjectRoot() {
        return this.projectRoot;
    }

    public @NotNull Gson getGson() {
        return this.gson;
    }

    public @NotNull PackMeta getMeta() {
        return this.meta;
    }

    public void loadProject() throws IOException {
        this.meta.load();
        buildFileTree();
    }

    public void unloadProject() {
        this.fileTree.clean();
    }

    private void buildFileTree() throws IOException {
        this.fileTree.build();
    }

    public @NotNull FileTree getFileTree() {
        return this.fileTree;
    }
}
