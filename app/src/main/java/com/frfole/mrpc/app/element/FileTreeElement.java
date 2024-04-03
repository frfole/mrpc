package com.frfole.mrpc.app.element;

import com.frfole.mrpc.app.texture.PackTexture;
import com.frfole.mrpc.app.texture.AppTexture;
import com.frfole.mrpc.app.texture.Textures;
import com.frfole.mrpc.app.translation.I18n;
import com.frfole.mrpc.pack.filetree.FileTreeNode;
import com.frfole.mrpc.pack.resource.ResourceKind;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An element to make showing {@link FileTreeNode} a bit easier.
 */
public final class FileTreeElement {
    // Icons for some files
    private static final AppTexture ICON_FOLDER = Textures.getAppTexture("/images/filetree/folder.png");
    private static final AppTexture ICON_FILE = Textures.getAppTexture("/images/filetree/file.png");
    private static final AppTexture ICON_NAMESPACE = Textures.getAppTexture("/images/filetree/namespace.png");
    private static final AppTexture ICON_PACK_META = Textures.getAppTexture("/images/filetree/packmeta.png");

    /**
     * Draws tree node as a tree.
     * @param node the node to begin with
     * @param openCallback called when file (not directory) is opened with associated node
     */
    public static void tree(@NotNull FileTreeNode node, @NotNull Consumer<@NotNull FileTreeNode> openCallback) {
        // show entry icon
        TooltipElement.tooltip(() -> showIcon(node), () -> showTooltip(node));

        // make tree node flags
        int treeNodeFlags = ImGuiTreeNodeFlags.SpanFullWidth
                | (node.getKind() == ResourceKind.PROJECT_ROOT ? ImGuiTreeNodeFlags.DefaultOpen : ImGuiTreeNodeFlags.None) // there is no need to have root folder collapsed
                | (node.isFile() ? ImGuiTreeNodeFlags.Leaf : ImGuiTreeNodeFlags.None);

        // show tree node
        ImGui.sameLine();
        if (ImGui.treeNodeEx(node.getName(), treeNodeFlags)) {
            if (ImGui.isItemClicked() && node.isFile()) {
                openCallback.accept(node);
            }
            // show child entries
            for (FileTreeNode child : node.getChildren()) {
                tree(child, openCallback);
            }
            ImGui.treePop();
        }
    }

    private static void showIcon(@NotNull FileTreeNode node) {
        ImGui.image(switch (node.getKind()) {
            case PROJECT_ROOT -> {
                PackTexture icon = Textures.getPackIcon();
                if (icon == null)
                    yield ICON_FOLDER.getId();
                yield icon.getId();
            }
            case DIRECTORY -> ICON_FOLDER.getId();
            case NAMESPACE -> ICON_NAMESPACE.getId();
            case PACK_META -> ICON_PACK_META.getId();
            case PACK_ICON -> {
                PackTexture icon = Textures.getPackIcon();
                if (icon == null)
                    yield ICON_FILE.getId();
                yield icon.getId();
            }
            case TEXTURE -> Textures.getPackTexture(node.getRelativePath()).getId();
            case GENERIC_FILE -> ICON_FILE.getId();
        }, 16, 16);
    }

    private static void showTooltip(@NotNull FileTreeNode node) {
        ImGui.textDisabled(node.getRelativePath().toString());
        switch (node.getKind()){
            case PROJECT_ROOT -> {
                ImGui.text(I18n.translate("element.file_tree.icon.project_root"));
                PackTexture icon = Textures.getPackIcon();
                if (icon != null) {
                    ImGui.text(I18n.translate("element.file_tree.icon_info.size", icon.getWidth(), icon.getHeight()));
                    ImGui.image(icon.getId(), icon.getWidth(), icon.getHeight());
                }
            }
            case DIRECTORY -> ImGui.text(I18n.translate("element.file_tree.icon.directory"));
            case NAMESPACE -> ImGui.text(I18n.translate("element.file_tree.icon.namespace"));
            case PACK_META -> ImGui.text(I18n.translate("element.file_tree.icon.pack_meta"));
            case PACK_ICON -> {
                ImGui.text(I18n.translate("element.file_tree.icon.pack_icon"));
                PackTexture icon = Textures.getPackIcon();
                if (icon != null) {
                    ImGui.text(I18n.translate("element.file_tree.icon_help.size", icon.getWidth(), icon.getHeight()));
                    ImGui.image(icon.getId(), icon.getWidth(), icon.getHeight());
                }
            }
            case TEXTURE -> {
                ImGui.text(I18n.translate("element.file_tree.icon.texture"));
                PackTexture texture = Textures.getPackTexture(node.getRelativePath());
                ImGui.text(I18n.translate("element.file_tree.icon_help.size", texture.getWidth(), texture.getHeight()));
                ImGui.image(texture.getId(), texture.getWidth(), texture.getHeight());
            }
            case GENERIC_FILE -> ImGui.text(I18n.translate("element.file_tree.icon.generic_file"));
        }
    }
}
