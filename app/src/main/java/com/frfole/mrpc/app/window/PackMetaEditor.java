package com.frfole.mrpc.app.window;

import com.frfole.mrpc.app.ExceptionHandler;
import com.frfole.mrpc.app.element.TooltipElement;
import com.frfole.mrpc.app.element.component.ComponentInputElement;
import com.frfole.mrpc.app.element.component.ComponentOutputElement;
import com.frfole.mrpc.app.translation.I18n;
import com.frfole.mrpc.pack.Project;
import com.frfole.mrpc.pack.meta.PackMeta;
import com.frfole.mrpc.pack.text.MutableComponent;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PackMetaEditor implements Window {
    private final Project project;
    private final ImInt formatNative = new ImInt(PackMeta.DEFAULT_FORMAT);
    private final ImInt formatMin = new ImInt(PackMeta.DEFAULT_FORMAT);
    private final ImInt formatMax = new ImInt(PackMeta.DEFAULT_FORMAT);
    private final ComponentInputElement description;

    public PackMetaEditor(@NotNull Project project) {
        this.project = project;
        this.description = new ComponentInputElement(MutableComponent.fromAdventure(project.getMeta().getDescription()));
        formatNative.set(this.project.getMeta().getFormatNative());
        formatMin.set(this.project.getMeta().getFormatMin());
        formatMax.set(this.project.getMeta().getFormatMax());
    }

    @Override
    public void draw() {
        if (ImGui.begin(I18n.translate("editor.pack_meta.title"))) {

            // format section
            ImGui.text(I18n.translate("editor.pack_meta.format"));
            ImGui.sameLine();
            TooltipElement.tooltip(400f, () -> ImGui.text(I18n.translate("editor.pack_meta.format.info")));
            // TODO: list Minecraft versions and their resource pack format

            // native format input
            ImGui.indent();
            ImGui.pushItemWidth(100f);
            if (ImGui.inputInt(I18n.translate("editor.pack_meta.format.native"), formatNative, 1, 5)) {
                if (formatNative.get() < 0) formatNative.set(0);
                if (formatMin.get() > formatNative.get()) formatMin.set(formatNative);
                if (formatMax.get() < formatNative.get()) formatMax.set(formatNative);
                this.project.getMeta().setFormatNative(formatNative.get());
            }
            ImGui.sameLine();
            TooltipElement.tooltip(400f, () -> ImGui.text(I18n.translate("editor.pack_meta.format.native.info")));

            // minimal supported format input
            if (ImGui.inputInt(I18n.translate("editor.pack_meta.format.min"), formatMin, 1, 5)) {
                if (formatMin.get() < 0) formatMin.set(0);
                else if (formatMin.get() > formatNative.get()) formatMin.set(formatNative);
                this.project.getMeta().setFormatMin(formatMin.get());
            }
            ImGui.sameLine();
            TooltipElement.tooltip(400f, () -> ImGui.text(I18n.translate("editor.pack_meta.format.min.info")));

            // maximal supported format input
            if (ImGui.inputInt(I18n.translate("editor.pack_meta.format.max"), formatMax, 1, 5)) {
                if (formatMax.get() < formatNative.get()) formatMax.set(formatNative);
                this.project.getMeta().setFormatMax(formatMax.get());
            }
            ImGui.sameLine();
            TooltipElement.tooltip(400f, () -> ImGui.text(I18n.translate("editor.pack_meta.format.max.info")));
            ImGui.popItemWidth();
            ImGui.unindent();

            // description section
            ImGui.text(I18n.translate("editor.pack_meta.description"));
            ImGui.sameLine();
            TooltipElement.tooltip(400f, () -> ImGui.text(I18n.translate("editor.pack_meta.description.info")));

            // description preview
            ImGui.indent();
            TooltipElement.tooltip(400f,
                    () -> ImGui.text(I18n.translate("editor.pack_meta.description.preview")),
                    () -> ImGui.text(I18n.translate("editor.pack_meta.description.preview.info")));
            ImGui.indent();
            ComponentOutputElement.drawMutableComponent(this.description.getComponent());
            ImGui.unindent();

            // description editor
            ImGui.text(I18n.translate("editor.pack_meta.description.editor"));
            ImGui.indent();
            description.draw();
            ImGui.unindent();
            ImGui.unindent();

            // TODO: show alert if trying to close if not saved
            // save button
            if (ImGui.button(I18n.translate("editor.common.save"))) {
                try {
                    this.project.getMeta().setDescription(this.description.getComponent().toAdventure());
                    this.project.getMeta().save();
                } catch (IOException e) {
                    ExceptionHandler.handleException(e);
                }
            }
        }
        ImGui.end();
    }
}
