package com.frfole.mrpc.app.view;

import com.frfole.mrpc.app.MRPCApp;
import com.frfole.mrpc.app.config.RecentProject;
import com.frfole.mrpc.app.translation.I18n;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StartupView implements View {
    private final MRPCApp app;

    public StartupView(MRPCApp app) {
        this.app = app;
    }

    @Override
    public void onOpen() {
        View.super.onOpen();
        List<RecentProject> newRecentProjects = new ArrayList<>();
        for (RecentProject project : app.getConfig().recentProjects()) {
            if (Files.exists(project.path())) {
                newRecentProjects.add(project);
            }
        }
        if (newRecentProjects.size() != app.getConfig().recentProjects().size()) {
            app.updateConfig(config -> config.withRecentProjects(newRecentProjects));
        }
    }

    @Override
    public void process() {
        View.super.process();
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);
        ImGui.setNextWindowSizeConstraints(400f, 500f, Float.MAX_VALUE, Float.MAX_VALUE);
        if (ImGui.begin(I18n.translate("view.startup.select_project"), ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking)) {
            if (ImGui.button("test view")) {
                app.setView(new TestView());
            }

            if (ImGui.button(I18n.translate("view.startup.open_project"))) {
                ImGuiFileDialog.openModal("ProjectFileSelector", I18n.translate("view.startup.select_packmeta"), ".mcmeta", "", "", 1, 0, ImGuiFileDialogFlags.None);
                ImGui.setWindowFocus("ProjectFileSelector");
            }
            ImGui.sameLine();
            if (ImGui.button(I18n.translate("view.startup.new_project"))) {
                ImGuiFileDialog.openModal("ProjectFolderSelector", I18n.translate("view.startup.select_project_folder"), null, "", "", 1, 0, ImGuiFileDialogFlags.None);
                ImGui.setWindowFocus("ProjectFolderSelector");
                ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
            }
            ImGui.text(I18n.translate("view.startup.recent_projects"));
            if (ImGui.beginListBox("##RecentProjects", -1f, -1f)) {
                for (RecentProject project : app.getConfig().recentProjects()) {
                    if (ImGui.selectable(project.path().toString())) {
                        app.openProject(project.path());
                    }
                }
                ImGui.endListBox();
            }
        }
        ImGui.end();
        if (ImGuiFileDialog.display("ProjectFileSelector", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking, 400, 300, 400, 300)) {
            if (ImGuiFileDialog.isOk()) {
                app.openProject(Path.of(ImGuiFileDialog.getCurrentPath()));
            }
            ImGuiFileDialog.close();
        }
        if (ImGuiFileDialog.display("ProjectFolderSelector", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking, 400, 300, 400, 300)) {
            if (ImGuiFileDialog.isOk()) {
                app.openProject(Path.of(ImGuiFileDialog.getCurrentPath()));
            }
            ImGui.getIO().addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
            ImGuiFileDialog.close();
        }
    }
}
