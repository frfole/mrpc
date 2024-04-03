package com.frfole.mrpc.app.view;

import com.frfole.mrpc.app.MRPCApp;
import com.frfole.mrpc.app.element.FileTreeElement;
import com.frfole.mrpc.app.translation.I18n;
import com.frfole.mrpc.app.window.PackMetaEditor;
import com.frfole.mrpc.app.window.Window;
import com.frfole.mrpc.pack.filetree.FileTree;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.type.ImInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectView implements View {
    private final MRPCApp app;
    private final ImInt dockRoot = new ImInt();
    private final ImInt dockLeft = new ImInt();
    private final ImInt dockRight = new ImInt();
    private @Nullable Window editorWindow = null;
    private boolean firstProcess = true;

    public ProjectView(@NotNull MRPCApp app) {
        this.app = app;
    }

    @Override
    public void onOpen() {
        View.super.onOpen();
        firstProcess = true;
    }

    @Override
    public void onClose() {
        View.super.onClose();
        imgui.internal.ImGui.dockBuilderRemoveNode(dockRoot.get());
    }

    @Override
    public void process() {
        View.super.process();

        // setup docking
        dockRoot.set(ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.None));
        if (firstProcess) {
            firstProcess = false;
            imgui.internal.ImGui.dockBuilderSplitNode(dockRoot.get(), ImGuiDir.Left, 0.3f, dockLeft, dockRoot);
            imgui.internal.ImGui.dockBuilderSplitNode(dockRoot.get(), ImGuiDir.Right, 0.4f, dockRight, dockRoot);
            imgui.internal.ImGui.dockBuilderFinish(dockRoot.get());
        }

        // render main menu bar
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu(I18n.translate("view.project.menu.file"))) {
                if (ImGui.menuItem(I18n.translate("view.project.menu.file.close_project"))) {
                    app.closeProject();
                }
                ImGui.endMenu();
            }
            ImGui.endMainMenuBar();
        }
        // don't run further, if we had closed the project using the main menu
        if (app.getActiveProject() == null) {
            return;
        }

        // render project tree
        ImGui.setNextWindowDockID(dockLeft.get(), ImGuiCond.Appearing);
        if (ImGui.begin(I18n.translate("view.project.project_tree"))) {
            FileTree fileTree = app.getActiveProject().getFileTree();
            FileTreeElement.tree(fileTree.getRootNode(), node -> {
                if (node.getName().equals("pack.mcmeta")) {
                    editorWindow = new PackMetaEditor(this.app.getActiveProject());
                }
                System.out.println("open file: " + node.getRelativePath());
            });
        }
        ImGui.end();

        // render editor window
        if (editorWindow != null) {
            ImGui.setNextWindowDockID(dockRoot.get(), ImGuiCond.Appearing);
            editorWindow.draw();
        }

        ImGui.setNextWindowDockID(dockRight.get(), ImGuiCond.Appearing);
        ImGui.showMetricsWindow();
    }
}
