package com.frfole.mrpc.app.element.component;

import com.frfole.mrpc.pack.text.MutableComponent;
import com.frfole.mrpc.pack.text.style.MutableStyleColor;
import imgui.type.ImInt;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public abstract class ComponentData<T extends MutableComponent> {
    protected final T component;
    private final float[] color = new float[]{0, 0, 0};
    private final EnumMap<TextDecoration, ImInt> decorations = new EnumMap<>(TextDecoration.class);

    protected ComponentData(T component) {
        this.component = component;
        loadColor();
        for (TextDecoration type : TextDecoration.values()) {
            decorations.put(type, new ImInt(switch (component.getStyle().getDecorations().getState(type)) {
                case NOT_SET -> 0;
                case TRUE -> 1;
                case FALSE -> 2;
            }));
        }
    }

    public @NotNull T getComponent() {
        return component;
    }

    public float @NotNull [] getColor() {
        return this.color;
    }

    public @NotNull ImInt getDecoration(@NotNull TextDecoration type) {
        return decorations.get(type);
    }

    public void loadColor() {
        MutableStyleColor color = getComponent().getStyle().getColor();
        this.color[2] = color.getRed() / 255f;
        this.color[1] = color.getGreen() / 255f;
        this.color[0] = color.getBlue() / 255f;
    }

    public void saveColor() {
        getComponent().getStyle().getColor().setColor((int) (this.color[2] * 255), (int) (this.color[1] * 255), (int) (this.color[0] * 255));
    }

    public void saveDecoration(@NotNull TextDecoration type) {
        getComponent().getStyle().getDecorations().setState(type, switch (decorations.get(type).get()) {
            case 1 -> TextDecoration.State.TRUE;
            case 2 -> TextDecoration.State.FALSE;
            default -> TextDecoration.State.NOT_SET;
        });
    }

    abstract void save();
}
