package com.frfole.mrpc.app.view;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class ExceptionView implements View {
    private final Exception exception;

    public ExceptionView(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void process() {
        View.super.process();
        if (ImGui.begin("An exception occurred", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text(exception.getClass() + ": " + exception.getMessage());
            ImGui.beginGroup();
            for (StackTraceElement element : exception.getStackTrace()) {
                ImGui.text("  at " + element.toString());
            }
            ImGui.endGroup();
        }
        ImGui.end();
    }
}
