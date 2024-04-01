package com.frfole.mrpc.app.element.component;

import com.frfole.mrpc.pack.text.MutableComponent;
import com.frfole.mrpc.pack.text.MutableTextComponent;
import imgui.ImGui;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class ComponentOutputElement {
    public static void drawMutableComponent(@NotNull MutableComponent component) {
        drawMutableComponent(component, Style.empty());
    }

    public static void drawMutableComponent(@NotNull MutableComponent component, @NotNull Style parentStyle) {
        // get style of the component
        Style style = component.getStyle().toAdventure().merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        TextColor color = style.color();
        if (color == null) color = TextColor.color(0xffffff);

        // draw the component
        switch (component) {
            case MutableTextComponent textComponent -> ImGui.textColored(0xff000000 | color.value(), textComponent.getContent());
            default -> throw new RuntimeException("component type not supported: " + component.getClass());
        }

        // draw child components
        for (MutableComponent child : component.getChildren()) {
            ImGui.sameLine();
            drawMutableComponent(child, style);
        }
    }
}
