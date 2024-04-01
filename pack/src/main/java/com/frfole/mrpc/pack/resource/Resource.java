package com.frfole.mrpc.pack.resource;

import com.frfole.mrpc.pack.Project;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class Resource {
    private final Project project;

    public Resource(@NotNull Project project) {
        this.project = project;
    }

    protected @NotNull Project getProject() {
        return this.project;
    }

    public abstract void load(@NotNull BufferedReader reader) throws IOException;
    public abstract void save(@NotNull BufferedWriter writer) throws IOException;

    public void load() throws IOException {}
    public void save() throws IOException {}
}
