package com.frfole.mrpc.pack.text;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class MutableTextComponent extends MutableComponent {
    private @NotNull String content;

    public MutableTextComponent(@NotNull String content) {
        this.content = content;
    }

    public MutableTextComponent() {
        this("");
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public @NotNull String getContent() {
        return this.content;
    }

    @Override
    public @NotNull Component toAdventure0() {
        return Component.text(content);
    }
}
