package com.frfole.mrpc.pack.resource;

import com.frfole.mrpc.pack.Project;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class FileResource extends Resource {
    private final Path backingFile;

    protected FileResource(@NotNull Path backingFile, @NotNull Project project) {
        super(project);
        this.backingFile = backingFile;
    }

    @Override
    public void load() throws IOException {
        BufferedReader reader = Files.newBufferedReader(backingFile);
        load(reader);
        reader.close();
    }

    @Override
    public void save() throws IOException {
        Files.createDirectories(backingFile.getParent());
        BufferedWriter writer = Files.newBufferedWriter(backingFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        save(writer);
        writer.flush();
        writer.close();
    }

    public @NotNull Path getBackingFile() {
        return this.backingFile;
    }
}
