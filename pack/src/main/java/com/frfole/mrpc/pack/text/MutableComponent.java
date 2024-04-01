package com.frfole.mrpc.pack.text;

import com.frfole.mrpc.pack.text.style.MutableStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MutableComponent {
    private final List<@NotNull MutableComponent> children = new ArrayList<>();
    private final MutableStyle style = new MutableStyle();

    public @NotNull List<@NotNull MutableComponent> getChildren() {
        return children;
    }

    public static MutableComponent fromAdventure(@NotNull Component original) {
        MutableComponent component = switch (original) {
            case TextComponent a -> new MutableTextComponent(a.content());
            default -> empty();
        };
        component.style.fromAdventure(original.style());
        for (Component child : original.children()) {
            component.children.add(fromAdventure(child));
        }
        return component;
    }

    public static MutableComponent empty() {
        return new MutableTextComponent();
    }

    protected abstract @NotNull Component toAdventure0();

    public @NotNull Component toAdventure() {
        List<Component> newChildren = new ArrayList<>();
        for (MutableComponent child : this.children) {
            newChildren.add(child.toAdventure());
        }
        return toAdventure0()
                .style(style.toAdventure())
                .children(newChildren);
    }

    public @NotNull MutableStyle getStyle() {
        return this.style;
    }
}
