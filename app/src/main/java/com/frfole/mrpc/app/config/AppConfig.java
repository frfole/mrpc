package com.frfole.mrpc.app.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds required application configuration.
 * @param recentProjects chronologically sorted list of {@link RecentProject}s
 */
@JsonAdapter(AppConfig.AppConfigAdapter.class)
public record AppConfig(@NotNull List<RecentProject> recentProjects) {

    public AppConfig {
        recentProjects = List.copyOf(recentProjects);
    }

    /**
     * Pushes a new {@link RecentProject} to the first spot among recent projects.
     * @param project the new recent project to add
     * @return a new config with given recent project added
     */
    public AppConfig withNewRecentProject(@NotNull RecentProject project) {
        List<RecentProject> newRecentProjects = new ArrayList<>();
        newRecentProjects.add(project);
        for (RecentProject recentProject : recentProjects) {
            boolean sameFile = false;
            try {
                sameFile = Files.isSameFile(recentProject.path(), project.path());
            } catch (IOException exception) {
                // TODO: create window for less critical exceptions
                exception.printStackTrace();
            }
            if (!sameFile) {
                newRecentProjects.add(recentProject);
            }
        }
        return new AppConfig(newRecentProjects);
    }

    /**
     * Replace list of recent projects with new ones.
     * @param newRecentProjects the new recent projects list
     * @return a new config with given recent projects
     */
    public AppConfig withRecentProjects(@NotNull List<RecentProject> newRecentProjects) {
        return new AppConfig(newRecentProjects);
    }

    public static class AppConfigAdapter extends TypeAdapter<AppConfig> {

        @Override
        public void write(JsonWriter out, AppConfig value) throws IOException {
            out.beginObject();
            if (value == null) {
                out.endObject();
                return;
            }
            out.name("recent_projects");
            out.beginArray();
            for (RecentProject project : value.recentProjects) {
                out.beginObject();
                out.name("path");
                out.value(project.path().toAbsolutePath().toString());
                out.endObject();
            }
            out.endArray();
            out.endObject();
        }

        @Override
        public AppConfig read(JsonReader in) {
            List<RecentProject> recentProjects = new ArrayList<>();
            JsonObject jo = JsonParser.parseReader(in).getAsJsonObject();
            JsonArray jRecentProjects = jo.getAsJsonArray("recent_projects");
            if (jRecentProjects != null) {
                for (JsonElement jRecentProject : jRecentProjects) {
                    JsonObject jRecentProjectObj = jRecentProject.getAsJsonObject();
                    String path = jRecentProjectObj.get("path").getAsString();
                    recentProjects.add(new RecentProject(Path.of(path)));
                }
            }
            return new AppConfig(recentProjects);
        }
    }
}
