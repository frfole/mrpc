package com.frfole.mrpc.pack.resource;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface Resource {

    /**
     * Attempts to load a resource from the reader
     * @param reader source to load from
     */
    void load(@NotNull BufferedReader reader) throws IOException;

    /**
     * Attempts to save a resource to the writer.
     * @param writer the writer to save to
     */
    void save(@NotNull BufferedWriter writer) throws IOException;

    /**
     * Called to load the resource.
     */
    default void load() throws IOException {}

    /**
     * Called to save the resource.
     */
    default void save() throws IOException {}
}
