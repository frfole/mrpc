package com.frfole.mrpc.pack.meta;

import com.frfole.mrpc.pack.Project;
import com.frfole.mrpc.pack.resource.FileResource;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Meta extends FileResource {
    protected Meta(@NotNull Path backingFile) {
        super(backingFile);
    }

    abstract void loadJson(@NotNull JsonElement je);
    abstract void saveJson(@NotNull JsonWriter writer) throws IOException;

    @Override
    public void load(@NotNull BufferedReader reader) throws IOException {
        JsonElement je = JsonParser.parseReader(reader);
        loadJson(je);
        save();
    }

    @Override
    public void save(@NotNull BufferedWriter writer) throws IOException {
        saveJson(new JsonWriter(writer));
    }
}
