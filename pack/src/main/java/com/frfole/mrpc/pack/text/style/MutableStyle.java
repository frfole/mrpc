package com.frfole.mrpc.pack.text.style;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class MutableStyle {
    private final MutableStyleColor color = new MutableStyleColor();
    private final MutableStyleDecoration decoration = new MutableStyleDecoration();

    public static MutableStyle newFromAdventure(@NotNull Style original) {
        MutableStyle style = new MutableStyle();
        style.fromAdventure(original);
        return style;
    }

    public void fromAdventure(@NotNull Style original) {
        TextColor oColor = original.color();
        if (oColor != null) {
            color.setColor(oColor.red(), oColor.green(), oColor.blue());
        } else {
            color.unset();
        }
    }

    public Style toAdventure() {
        Style style = Style.style(color.toAdventure());
        for (TextDecoration type : TextDecoration.values()) {
            style = style.decoration(type, decoration.getState(type));
        }
        return style;
    }

    public @NotNull MutableStyleColor getColor() {
        return this.color;
    }

    public @NotNull MutableStyleDecoration getDecorations() {
        return this.decoration;
    }
}
