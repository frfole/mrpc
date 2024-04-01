package com.frfole.mrpc.pack.text.style;

import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class MutableStyleDecoration {
    private TextDecoration.State obfuscated = TextDecoration.State.NOT_SET;
    private TextDecoration.State bold = TextDecoration.State.NOT_SET;
    private TextDecoration.State strikethrough = TextDecoration.State.NOT_SET;
    private TextDecoration.State underline = TextDecoration.State.NOT_SET;
    private TextDecoration.State italic = TextDecoration.State.NOT_SET;

    public TextDecoration.State getState(@NotNull TextDecoration type) {
        return switch (type) {
            case OBFUSCATED -> obfuscated;
            case BOLD -> bold;
            case STRIKETHROUGH -> strikethrough;
            case UNDERLINED -> underline;
            case ITALIC -> italic;
        };
    }

    public void setState(@NotNull TextDecoration type, @NotNull TextDecoration.State state) {
        switch (type) {
            case OBFUSCATED -> obfuscated = state;
            case BOLD -> bold = state;
            case STRIKETHROUGH -> strikethrough = state;
            case UNDERLINED -> underline = state;
            case ITALIC -> italic = state;
        }
    }
}
