package com.frfole.mrpc.app;

import com.frfole.mrpc.app.view.ExceptionView;
import imgui.ImGui;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExceptionHandler {
    private static final List<ExceptionEntry> EXCEPTION_ENTRIES = new CopyOnWriteArrayList<>();

    public static void handleException(@NotNull Exception exception, boolean isFatal) {
        EXCEPTION_ENTRIES.add(new ExceptionEntry(exception, isFatal));
        exception.printStackTrace();
    }

    public static void handleException(@NotNull Exception exception) {
        handleException(exception, false);
    }

    public static void draw(@NotNull MRPCApp app) {
        for (int i = 0; i < EXCEPTION_ENTRIES.size(); i++) {
            ExceptionEntry entry = EXCEPTION_ENTRIES.get(i);
            if (entry.isFatal) {
                app.setView(new ExceptionView(entry.exception));
                return;
            }
            if (ImGui.begin("An exception occurred##" + entry.exception.hashCode())) {
                ImGui.text(entry.exception.getClass() + ": " + entry.exception.getMessage());
                ImGui.beginGroup();
                for (StackTraceElement element : entry.exception.getStackTrace()) {
                    ImGui.text("  at " + element.toString());
                }
                ImGui.endGroup();
                if (ImGui.button("Close")) {
                    EXCEPTION_ENTRIES.remove(i);
                    i--;
                }
            }
            ImGui.end();
        }
    }

    protected record ExceptionEntry(@NotNull Exception exception, boolean isFatal) {}
}
