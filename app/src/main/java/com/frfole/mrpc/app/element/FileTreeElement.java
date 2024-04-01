package com.frfole.mrpc.app.element;

import com.frfole.mrpc.app.Textures;
import com.frfole.mrpc.app.translation.I18n;
import com.frfole.mrpc.pack.resource.ResourceKind;
import com.frfole.mrpc.pack.util.FileTree;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FileTreeElement {
    private static final Textures.Texture TEXTURE_FOLDER = Textures.getAppTexture("/images/filetree/folder.png");
    private static final Textures.Texture TEXTURE_FILE = Textures.getAppTexture("/images/filetree/file.png");
    private static final Textures.Texture TEXTURE_NAMESPACE = Textures.getAppTexture("/images/filetree/namespace.png");
    private static final Textures.Texture TEXTURE_PACK_META = Textures.getAppTexture("/images/filetree/packmeta.png");

    public static void tree(@NotNull FileTree.TreeNode node, @NotNull Consumer<FileTree.@NotNull TreeNode> openCallback) {
        // show entry icon
        TooltipElement.tooltip(
                () -> ImGui.image(switch (node.getKind()) {
                    case PROJECT_ROOT -> TEXTURE_FOLDER.getId(); // TODO: use pack icon
                    case DIRECTORY -> TEXTURE_FOLDER.getId();
                    case NAMESPACE -> TEXTURE_NAMESPACE.getId();
                    case PACK_META -> TEXTURE_PACK_META.getId();
                    case GENERIC_FILE -> TEXTURE_FILE.getId(); // TODO: use texture of texture
                }, 16, 16),
                () -> ImGui.text(switch (node.getKind()) {
                    case PROJECT_ROOT -> I18n.translate("element.file_tree.icon.project_root");
                    case DIRECTORY -> I18n.translate("element.file_tree.icon.directory");
                    case NAMESPACE -> I18n.translate("element.file_tree.icon.namespace");
                    case PACK_META -> I18n.translate("element.file_tree.icon.pack_meta");
                    case GENERIC_FILE -> I18n.translate("element.file_tree.icon.generic_file");
                }));
        ImGui.sameLine();

        // show entry
        int treeNodeFlags = ImGuiTreeNodeFlags.SpanFullWidth
                | (node.getKind() == ResourceKind.PROJECT_ROOT ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None) // there is no need to have root folder collapsed
                | (node.isFile() ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.None);
        if (ImGui.treeNodeEx(node.getName(), treeNodeFlags)) {
            if (ImGui.isItemClicked() && node.isFile()) {
                openCallback.accept(node);
            }
            // show child entries
            for (FileTree.TreeNode child : node.getChildren()) {
                tree(child, openCallback);
            }
            ImGui.treePop();
        }
    }
}
