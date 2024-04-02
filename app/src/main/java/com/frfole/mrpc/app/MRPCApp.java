package com.frfole.mrpc.app;

import com.frfole.mrpc.app.config.AppConfig;
import com.frfole.mrpc.app.config.ConfigUtils;
import com.frfole.mrpc.app.config.RecentProject;
import com.frfole.mrpc.app.texture.Textures;
import com.frfole.mrpc.app.view.ExceptionView;
import com.frfole.mrpc.app.view.ProjectView;
import com.frfole.mrpc.app.view.StartupView;
import com.frfole.mrpc.app.view.View;
import com.frfole.mrpc.pack.Project;
import com.frfole.mrpc.pack.filetree.ChangeEntry;
import com.frfole.mrpc.pack.resource.ResourceKind;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Function;

public final class MRPCApp extends Application {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @NotNull View openedView = null;
    private @Nullable View nextView = null;
    private @NotNull AppConfig appConfig = new AppConfig(List.of());
    private @Nullable Project activeProject = null;

    public boolean setView(@NotNull View newView) {
        if (openedView instanceof ExceptionView) return false;
        nextView = newView;
        return true;
    }

    public @NotNull AppConfig getConfig() {
        return this.appConfig;
    }

    public void updateConfig(@NotNull AppConfig config) {
        this.appConfig = config;
        Path configPath = ConfigUtils.configRoot.resolve("config.json");
        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
        try (BufferedWriter bufWriter = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE)) {
            this.gson.toJson(appConfig, AppConfig.class, bufWriter);
            bufWriter.flush();
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public void updateConfig(Function<AppConfig, AppConfig> updater) {
        updateConfig(updater.apply(appConfig));
    }

    public void closeProject() {
        if (activeProject == null) return;
        activeProject.unloadProject();
        activeProject = null;
        setView(new StartupView(this));
    }

    public void openProject(@NotNull Path path) {
        closeProject();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
        updateConfig(appConfig.withNewRecentProject(new RecentProject(path)));
        activeProject = new Project(path, gson);
        if (setView(new ProjectView(this))) {
            try {
                activeProject.loadProject();
            } catch (IOException e) {
                ExceptionHandler.handleException(e, true);
            }
        }
    }

    public @Nullable Project getActiveProject() {
        return this.activeProject;
    }

    @Override
    protected void configure(Configuration config) {
        super.configure(config);
        config.setTitle("MRPC");
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigWindowsMoveFromTitleBarOnly(true);
    }

    @Override
    protected void preRun() {
        super.preRun();
        Path configPath = ConfigUtils.configRoot.resolve("config.json");
        if (Files.exists(configPath)) {
            try (BufferedReader bufReader = Files.newBufferedReader(configPath)) {
                AppConfig appConfig = gson.fromJson(bufReader, AppConfig.class);
                if (appConfig == null) {
                    updateConfig(this.appConfig);
                } else {
                    this.appConfig = appConfig;
                }
            } catch (IOException e) {
                setView(new ExceptionView(e));
            }
        } else {
            updateConfig(this.appConfig);
        }
    }

    @Override
    public void process() {
        // swap views
        if (openedView == null) {
            setView(new StartupView(this));
            openedView = nextView;
            nextView = null;
            openedView.onOpen();
        } else if (nextView != null) {
            openedView.onClose();
            openedView = nextView;
            nextView = null;
            openedView.onOpen();
        }

        // check file tree for changes
        if (activeProject != null) {
            try {
                activeProject.getFileTree().checkChanges(this::onFileChange);
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }

        // process view
        openedView.process();

        if (!(openedView instanceof ExceptionView)) {
            ExceptionHandler.draw(this);
        }
    }

    private void onFileChange(@NotNull ChangeEntry change) {
        if (change.node().getKind() == ResourceKind.PACK_ICON) {
            if (change.type() == ChangeEntry.ChangeType.DELETE) Textures.removePackTexture(change.node().getPath());
            else Textures.getPackTexture(change.node().getPath()).reload();
        }
    }

    public static void main(String[] args) {
        launch(new MRPCApp());
    }
}
