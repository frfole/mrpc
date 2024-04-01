package com.frfole.mrpc.app.element;

import imgui.ImGui;
import org.jetbrains.annotations.NotNull;

public class TooltipElement {
    public static void tooltip(float width, @NotNull Runnable tooltip) {
        tooltip(width, () -> ImGui.textDisabled("(?)"), tooltip);
    }

    public static void tooltip(float width, @NotNull Runnable draw, @NotNull Runnable tooltip) {
        ImGui.beginGroup();
        draw.run();
        ImGui.endGroup();
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(width);
            tooltip.run();
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    public static void tooltip(@NotNull Runnable draw, @NotNull Runnable tooltip) {
        ImGui.beginGroup();
        draw.run();
        ImGui.endGroup();
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            tooltip.run();
            ImGui.endTooltip();
        }
    }
}
