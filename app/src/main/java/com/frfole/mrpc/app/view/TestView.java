package com.frfole.mrpc.app.view;

import com.frfole.mrpc.app.element.component.ComponentInputElement;
import com.frfole.mrpc.app.element.component.ComponentOutputElement;
import com.frfole.mrpc.pack.text.MutableComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import net.kyori.adventure.text.Component;

public class TestView implements View {
    private final MutableComponent component = MutableComponent.fromAdventure(
            Component.text("Hello world!")
    );
    private final ComponentInputElement inputElement = new ComponentInputElement(component);

    @Override
    public void process() {
        View.super.process();
        ImGui.setNextWindowSize(500f, 500f, ImGuiCond.Appearing);
        if (ImGui.begin("test")) {
            ComponentOutputElement.drawMutableComponent(component);
            if (ImGui.button("dump")) {
                System.out.println(inputElement.getComponent().toAdventure());
            }
            inputElement.draw();
        }
        ImGui.end();
        ImGui.showMetricsWindow();
    }
}
